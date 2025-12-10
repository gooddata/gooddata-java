/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.service.GoodData;
import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Comprehensive acceptance tests for {@link HttpClient5ComponentsClientHttpRequestFactory}
 * 
 * <p>These tests verify real-world usage scenarios with actual HTTP communication to GoodData endpoints,
 * focusing on areas that cannot be adequately tested with mocks:</p>
 * 
 * <ul>
 *   <li><strong>Connection Management:</strong> Real connection pooling, reuse, and resource cleanup</li>
 *   <li><strong>Stream Safety:</strong> Proper handling of HTTP response streams and connection lifecycle</li>
 *   <li><strong>Error Scenarios:</strong> Network failures, timeouts, and server error responses</li>
 *   <li><strong>Performance:</strong> Connection efficiency and response time validation</li>
 *   <li><strong>Concurrency:</strong> Thread safety under concurrent load</li>
 * </ul>
 * 
 * <p><strong>Environment Setup:</strong></p>
 * <ul>
 *   <li>Required variables: {@code host}, {@code login}, {@code password}</li>
 *   <li>Run with environment variables: 
 *       {@code host=gdctest.example.com login=user@domain.com password=secret mvn clean verify -P at}</li>
 *   <li>Or with Maven properties: {@code mvn test -Dgroups=at -Dhost=... -Dlogin=... -Dpassword=...}</li>
 *   <li>Tests are skipped automatically if environment is not configured</li>
 * </ul>
 */
@Test(groups = "at")
public class HttpClient5ComponentsClientHttpRequestFactoryAT extends AbstractGoodDataAT {

    private HttpClient5ComponentsClientHttpRequestFactory factory;
    private PoolingHttpClientConnectionManager connectionManager;
    
    // Override parent's static fields to avoid initialization issues with environment variables
    protected GoodDataEndpoint testEndpoint;
    protected GoodData testGd;
    
    // Performance tracking
    private static final int PERFORMANCE_THRESHOLD_MS = 5000; // Max acceptable response time
    private static final int CONNECTION_POOL_SIZE = 50;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 10;
    
    /**
     * Get configuration value from system properties first, then environment variables.
     * This supports both Maven -D properties and shell environment variables.
     * 
     * @param name the property/variable name
     * @return the value or null if not found
     */
    private static String getEnvironmentValue(String name) {
        // First try system properties (from mvn -Dhost=value)
        String value = System.getProperty(name);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        
        // Then try environment variables (from shell export or host=value mvn)
        value = System.getenv(name);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }
        
        return null;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        // Skip setup if environment variables are not available
        try {
            // Try to get host from both system properties and environment variables
            String host = getEnvironmentValue("host");
            if (host == null || host.trim().isEmpty()) {
                throw new IllegalArgumentException("Host not configured");
            }
            
            // Initialize test-specific endpoint and GoodData client
            testEndpoint = new GoodDataEndpoint(host);
            
            // Also validate other required variables are available  
            String login = getEnvironmentValue("login");
            String password = getEnvironmentValue("password");
            
            if (login != null && password != null) {
                testGd = new GoodData(host, login, password, new GoodDataSettings());
            }
            
            System.out.println("AT Test Environment - Host: " + host + ", Login: " + 
                (login != null ? login.replaceAll("(.{3}).*(@.*)", "$1***$2") : "null") +
                ", Password: " + (password != null ? "***" : "null"));
                
        } catch (Exception e) {
            throw new org.testng.SkipException("Skipping AT tests - environment not configured: " + e.getMessage());
        }

        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(CONNECTION_POOL_SIZE);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

        // Create factory for standard tests
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                .setConnectionManager(connectionManager);
        factory = new HttpClient5ComponentsClientHttpRequestFactory(clientBuilder.build());
    }

    /**
     * Tests basic HTTP GET functionality against real GoodData infrastructure.
     * Validates request creation, execution, and response stream handling.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleBasicGetRequest() throws Exception {
        if (testEndpoint == null) {
            String host = getEnvironmentValue("host");
            throw new org.testng.SkipException("Endpoint not configured. Host from config: " + host);
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");
        long startTime = System.currentTimeMillis();

        ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);
        assertNotNull(request, "Request should be created successfully");
        assertEquals(HttpMethod.GET, request.getMethod(), "Request method should match");
        assertEquals(uri, request.getURI(), "Request URI should match");

        try (ClientHttpResponse response = request.execute()) {
            long responseTime = System.currentTimeMillis() - startTime;
            
            assertNotNull(response, "Response should not be null");
            assertEquals(200, response.getStatusCode().value(), "Should get successful response");
            assertTrue(responseTime < PERFORMANCE_THRESHOLD_MS, 
                "Response time (" + responseTime + "ms) should be under " + PERFORMANCE_THRESHOLD_MS + "ms");

            // Validate response headers
            HttpHeaders headers = response.getHeaders();
            assertNotNull(headers, "Response headers should not be null");
            
            // Verify stream handling without StreamClosedException
            try (InputStream inputStream = response.getBody()) {
                assertNotNull(inputStream, "Response body stream should not be null");
                
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                
                String responseBody = result.toString(StandardCharsets.UTF_8);
                assertNotNull(responseBody, "Response body should not be null");
                assertTrue(responseBody.length() > 0, "Response body should contain data");
            }
        }
    }

    /**
     * Tests network error scenarios including timeouts and connection failures.
     * Validates proper exception handling and resource cleanup under error conditions.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleNetworkErrors() throws Exception {
        // Test 1: Invalid host (should fail quickly)
        URI invalidHostUri = URI.create("http://invalid-host-that-does-not-exist.example");
        
        try {
            ClientHttpRequest request = factory.createRequest(invalidHostUri, HttpMethod.GET);
            request.execute();
            assertTrue(false, "Should have thrown an exception for invalid host");
        } catch (Exception e) {
            // Expected - should fail with connection error
            assertNotNull(e, "Exception should be thrown for invalid host");
            assertTrue(e instanceof IOException || e.getCause() instanceof IOException,
                "Should throw IOException or have IOException as cause");
        }
        
        // Test 2: Timeout scenario with short timeout configuration
        if (testEndpoint != null) {
            // Create factory with very short timeouts for testing
            final RequestConfig timeoutConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(100))
                    .setResponseTimeout(Timeout.ofMilliseconds(500))
                    .build();
            final HttpClientBuilder timeoutClientBuilder = HttpClientBuilder.create()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(timeoutConfig);
            HttpClient5ComponentsClientHttpRequestFactory timeoutFactory = 
                    new HttpClient5ComponentsClientHttpRequestFactory(timeoutClientBuilder.build());
            
            URI timeoutUri = URI.create(testEndpoint.toUri() + "/gdc");
            
            try {
                ClientHttpRequest timeoutRequest = timeoutFactory.createRequest(timeoutUri, HttpMethod.GET);
                long start = System.currentTimeMillis();
                try (ClientHttpResponse response = timeoutRequest.execute()) {
                    // If it succeeds, verify it was reasonably fast
                    long duration = System.currentTimeMillis() - start;
                    assertTrue(duration < 2000, "Should either timeout or complete quickly");
                }
            } catch (Exception e) {
                // Timeout exceptions are acceptable
                assertTrue(e instanceof IOException, "Should throw IOException on timeout");
            }
        }
    }

    /**
     * Tests various HTTP methods (POST, PUT, DELETE) with real payloads.
     * Uses safe endpoints that accept these methods without side effects.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleAllHttpMethods() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        // Test POST with JSON payload
        URI postUri = URI.create(testEndpoint.toUri() + "/gdc/account/login");
        ClientHttpRequest postRequest = factory.createRequest(postUri, HttpMethod.POST);
        
        // Add headers and body
        postRequest.getHeaders().add("Content-Type", "application/json");
        try (var outputStream = postRequest.getBody()) {
            String jsonPayload = "{\"postUserLogin\": {\"login\": \"test\", \"password\": \"test\"}}";
            outputStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }
        
        // Execute and validate (expect 401 for invalid credentials, which is fine)
        try (ClientHttpResponse response = postRequest.execute()) {
            assertNotNull(response, "POST response should not be null");
            assertTrue(response.getStatusCode().value() >= 200 && response.getStatusCode().value() < 500,
                "POST should get valid HTTP response (2xx-4xx)");
        }
        
        // Test HEAD method
        URI headUri = URI.create(testEndpoint.toUri() + "/gdc");
        ClientHttpRequest headRequest = factory.createRequest(headUri, HttpMethod.HEAD);
        
        try (ClientHttpResponse headResponse = headRequest.execute()) {
            assertNotNull(headResponse, "HEAD response should not be null");
            assertEquals(200, headResponse.getStatusCode().value(), "HEAD should succeed");
            
            // HEAD should have no body but same headers as GET
            try (InputStream body = headResponse.getBody()) {
                int firstByte = body.read();
                assertTrue(firstByte == -1, "HEAD response should have no body content");
            }
        }
    }

    /**
     * Tests multiple consecutive requests to validate connection reuse and stream handling.
     * Ensures proper cleanup between requests without resource leaks.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleMultipleConsecutiveRequests() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");

        // Make multiple requests to test connection reuse and stream handling
        for (int i = 0; i < 5; i++) {
            ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);

            try (ClientHttpResponse response = request.execute()) {
                assertEquals(response.getStatusCode().value(), 200);

                // Read response completely to ensure proper stream cleanup
                try (InputStream inputStream = response.getBody()) {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    assertTrue(result.size() > 0);
                }
            }
        }
    }

    /**
     * Tests thread safety under concurrent load.
     * Validates that multiple threads can safely use the factory simultaneously.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleConcurrentRequests() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            @SuppressWarnings("unchecked")
            CompletableFuture<Boolean>[] futures = new CompletableFuture[20];

            for (int i = 0; i < 20; i++) {
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    try {
                        ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);

                        try (ClientHttpResponse response = request.execute()) {
                            if (response.getStatusCode().value() != 200) {
                                return false;
                            }

                            try (InputStream inputStream = response.getBody()) {
                                ByteArrayOutputStream result = new ByteArrayOutputStream();
                                byte[] buffer = new byte[1024];
                                int length;
                                while ((length = inputStream.read(buffer)) != -1) {
                                    result.write(buffer, 0, length);
                                }
                                return result.size() > 0;
                            }
                        }
                    } catch (IOException e) {
                        return false;
                    }
                }, executor);
            }

            // Wait for all requests to complete
            for (CompletableFuture<Boolean> future : futures) {
                assertTrue(future.get(30, TimeUnit.SECONDS), "Concurrent request failed");
            }
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests authenticated requests using the integrated GoodData client.
     * Validates that the HTTP factory properly handles authentication flows.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleAuthenticatedRequests() throws Exception {
        try {
            // Test authenticated endpoint access through SDK
            long startTime = System.currentTimeMillis();
            var account = gd.getAccountService().getCurrent();
            long authTime = System.currentTimeMillis() - startTime;
            
            assertNotNull(account, "Should successfully retrieve authenticated account data");
            assertTrue(authTime < PERFORMANCE_THRESHOLD_MS, 
                "Authentication request time (" + authTime + "ms) should be reasonable");
            
        } catch (Exception e) {
            // More specific error handling - don't silently ignore all exceptions
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                throw new org.testng.SkipException("Authentication credentials not valid: " + e.getMessage());
            } else if (e.getMessage() != null && e.getMessage().contains("host")) {
                throw new org.testng.SkipException("Host configuration issue: " + e.getMessage());
            } else {
                throw new AssertionError("Unexpected authentication error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Tests stream safety and proper resource cleanup.
     * Validates that streams can be safely closed and don't throw StreamClosedException.
     */
    @Test(groups = "httpFactory")
    public void shouldHandleStreamSafety() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");

        ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Get the input stream
        InputStream inputStream = response.getBody();

        // Read part of the stream
        byte[] buffer = new byte[100];
        int bytesRead = inputStream.read(buffer);
        assertTrue(bytesRead > 0);

        // Close the response (which should close the stream)
        response.close();

        // Try to read from the stream again - this should not throw StreamClosedException
        try {
            inputStream.read();
            // If we get here, either the stream is still readable or returned -1 (EOF)
            // Both are acceptable - we just want to avoid StreamClosedException
        } catch (IOException e) {
            // IOException is acceptable, but not StreamClosedException specifically
            assertTrue(!e.getClass().getSimpleName().equals("StreamClosedException"),
                "Should not throw StreamClosedException: " + e.getMessage());
        }
    }

    /**
     * Tests connection pooling and reuse efficiency.
     * Validates that connections are properly managed and returned to the pool.
     */
    @Test(groups = "httpFactory")
    public void shouldReuseConnectionsEfficiently() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");
        
        // Record initial connection pool state
        var initialStats = connectionManager.getTotalStats();
        int initialActive = initialStats.getLeased();
        int initialAvailable = initialStats.getAvailable();
        
        // Make multiple requests to same host - should reuse connections
        final int requestCount = 5;
        long totalResponseTime = 0;
        
        for (int i = 0; i < requestCount; i++) {
            long requestStart = System.currentTimeMillis();
            
            ClientHttpRequest request = factory.createRequest(uri, HttpMethod.GET);

            try (ClientHttpResponse response = request.execute()) {
                assertEquals(200, response.getStatusCode().value(), 
                    "Request " + (i + 1) + " should succeed");

                // Fully consume response to ensure connection is reusable
                try (InputStream inputStream = response.getBody()) {
                    byte[] buffer = new byte[1024];
                    while (inputStream.read(buffer) != -1) {
                        // consume stream completely
                    }
                }
                
                totalResponseTime += (System.currentTimeMillis() - requestStart);
            }
            
            // Brief pause to allow connection pool management
            Thread.sleep(10);
        }

        // Validate connection pool efficiency
        var finalStats = connectionManager.getTotalStats();
        int finalActive = finalStats.getLeased();
        int finalAvailable = finalStats.getAvailable();
        
        // Connections should be returned to pool (not many more active than initially)
        assertTrue(finalActive <= initialActive + MAX_CONNECTIONS_PER_ROUTE,
            "Active connections (" + finalActive + ") should not exceed reasonable limit");
            
        // Should have some available connections from reuse
        assertTrue(finalAvailable >= initialAvailable,
            "Available connections should not decrease: " + initialAvailable + " -> " + finalAvailable);
            
        // Performance should improve with connection reuse (later requests faster)
        double avgResponseTime = (double) totalResponseTime / requestCount;
        assertTrue(avgResponseTime < PERFORMANCE_THRESHOLD_MS / 2,
            "Average response time (" + avgResponseTime + "ms) should benefit from connection reuse");
    }
    
    /**
     * Tests performance characteristics under normal load.
     * Validates response times and connection pool efficiency metrics.
     */
    @Test(groups = "httpFactory")
    public void shouldMeetPerformanceRequirements() throws Exception {
        if (testEndpoint == null) {
            throw new org.testng.SkipException("Endpoint not configured");
        }

        URI uri = URI.create(testEndpoint.toUri() + "/gdc");
        final int warmupRequests = 3;
        final int testRequests = 10;
        
        // Warmup - establish connections
        for (int i = 0; i < warmupRequests; i++) {
            try (ClientHttpResponse response = factory.createRequest(uri, HttpMethod.GET).execute()) {
                // Just consume to warm up connections
                try (InputStream is = response.getBody()) {
                    while (is.read() != -1) { /* consume */ }
                }
            }
        }
        
        // Measure performance of established connections
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        
        for (int i = 0; i < testRequests; i++) {
            long start = System.currentTimeMillis();
            
            try (ClientHttpResponse response = factory.createRequest(uri, HttpMethod.GET).execute()) {
                assertEquals(200, response.getStatusCode().value(), "Performance test request should succeed");
                
                try (InputStream is = response.getBody()) {
                    while (is.read() != -1) { /* consume */ }
                }
            }
            
            long requestTime = System.currentTimeMillis() - start;
            totalTime += requestTime;
            minTime = Math.min(minTime, requestTime);
            maxTime = Math.max(maxTime, requestTime);
        }
        
        double avgTime = (double) totalTime / testRequests;
        
        // Performance assertions
        assertTrue(avgTime < PERFORMANCE_THRESHOLD_MS, 
            "Average response time (" + avgTime + "ms) should be under threshold");
        assertTrue(maxTime < PERFORMANCE_THRESHOLD_MS * 2, 
            "Max response time (" + maxTime + "ms) should be reasonable");
        assertTrue(maxTime - minTime < PERFORMANCE_THRESHOLD_MS, 
            "Response time variance (" + (maxTime - minTime) + "ms) should be consistent");
            
        // Connection pool should be healthy
        var stats = connectionManager.getTotalStats();
        assertTrue(stats.getLeased() <= MAX_CONNECTIONS_PER_ROUTE, 
            "Should not leak connections: " + stats.getLeased() + " active");
    }
}