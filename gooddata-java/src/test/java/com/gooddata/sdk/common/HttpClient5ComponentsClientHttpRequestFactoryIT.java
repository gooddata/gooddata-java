/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.StreamClosedException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.testng.Assert.assertEquals;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for {@link HttpClient5ComponentsClientHttpRequestFactory}
 * Tests real HTTP communication, stream handling, and error scenarios
 */
public class HttpClient5ComponentsClientHttpRequestFactoryIT {

    private HttpClient5ComponentsClientHttpRequestFactory factory;
    private CloseableHttpClient httpClient;
    private String baseUrl;

    @BeforeMethod
    void setUp() {
        initJadler().withDefaultResponseContentType("application/json");
        baseUrl = "http://localhost:" + port();
        httpClient = HttpClients.createDefault();
        factory = new HttpClient5ComponentsClientHttpRequestFactory(httpClient);
    }

    @AfterMethod
    void tearDown() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
        closeJadler();
    }

    @Test
    void shouldPerformGetRequest() throws IOException {
        // Given
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Hello World");
        responseData.put("status", "success");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/test")
                .respond()
                .withStatus(200)
                .withBody(new ObjectMapper().writeValueAsString(responseData));

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/test"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getStatusText());

        String responseBody = readResponseBody(response);
        assertTrue(responseBody.contains("Hello World"));
        assertTrue(responseBody.contains("success"));

        response.close();
    }

    @Test
    void shouldPerformPostRequestWithJsonBody() throws IOException {
        // Given
        Map<String, String> requestData = new HashMap<>();
        requestData.put("name", "Test User");
        requestData.put("email", "test@example.com");

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/api/users")
                .havingHeaderEqualTo("Content-Type", "application/json")
                .havingBodyEqualTo(new ObjectMapper().writeValueAsString(requestData))
                .respond()
                .withStatus(201)
                .withBody("{\"id\": 123, \"message\": \"User created\"}");

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/users"), HttpMethod.POST);
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = new ObjectMapper().writeValueAsString(requestData);
        request.getBody().write(jsonBody.getBytes(StandardCharsets.UTF_8));

        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Created", response.getStatusText());

        String responseBody = readResponseBody(response);
        assertTrue(responseBody.contains("123"));
        assertTrue(responseBody.contains("User created"));

        response.close();
    }

    @Test
    void shouldHandleCustomHeaders() throws IOException {
        // Given
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/secure")
                .havingHeaderEqualTo("Authorization", "Bearer token123")
                .havingHeaderEqualTo("X-Custom-Header", "custom-value")
                .respond()
                .withStatus(200)
                .withBody("{\"authenticated\": true}");

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/secure"), HttpMethod.GET);
        request.getHeaders().add("Authorization", "Bearer token123");
        request.getHeaders().add("X-Custom-Header", "custom-value");

        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(200, response.getStatusCode().value());
        String responseBody = readResponseBody(response);
        assertTrue(responseBody.contains("authenticated"));

        response.close();
    }

    @Test
    void shouldHandleErrorResponse() throws IOException {
        // Given
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/notfound")
                .respond()
                .withStatus(404)
                .withBody("{\"error\": \"Not Found\", \"message\": \"Resource not found\"}");

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/notfound"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not Found", response.getStatusText());

        String responseBody = readResponseBody(response);
        assertTrue(responseBody.contains("Resource not found"));

        response.close();
    }

    @Test
    void shouldHandleLargeResponseBody() throws IOException {
        // Given
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            largeContent.append("This is line ").append(i).append(" of a large response.\n");
        }

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/large")
                .respond()
                .withStatus(200)
                .withBody(largeContent.toString());

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/large"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(200, response.getStatusCode().value());

        String responseBody = readResponseBody(response);
        assertTrue(responseBody.length() > 100000);
        assertTrue(responseBody.contains("This is line 9999"));

        response.close();
    }

    @Test
    void shouldHandleEmptyResponse() throws IOException {
        // Given
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo("/api/resource/123")
                .respond()
                .withStatus(204);

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/resource/123"), HttpMethod.DELETE);
        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(204, response.getStatusCode().value());
        assertEquals("No Content", response.getStatusText());

        InputStream body = response.getBody();
        assertNotNull(body);
        assertEquals(0, body.available());

        response.close();
    }

    @Test
    void shouldHandleResponseHeaders() throws IOException {
        // Given
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/headers")
                .respond()
                .withStatus(200)
                .withHeader("X-Response-Id", "abc123")
                .withHeader("X-Rate-Limit", "1000")
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody("{\"data\": \"test\"}");

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/headers"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("abc123", response.getHeaders().getFirst("X-Response-Id"));
        assertEquals("1000", response.getHeaders().getFirst("X-Rate-Limit"));
        assertTrue(response.getHeaders().getFirst("Content-Type").contains("application/json"));

        response.close();
    }

    @Test
    void shouldHandleConnectionTimeout() throws IOException {
        // Given - Create request to non-responsive endpoint
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/slow")
                .respond()
                .withDelay(5000, TimeUnit.MILLISECONDS) // 5 second delay
                .withStatus(200)
                .withBody("{\"delayed\": true}");

        // When & Then
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/slow"), HttpMethod.GET);

        // This should work, but slowly
        ClientHttpResponse response = request.execute();
        assertNotNull(response);
        response.close();
    }

    @Test
    void shouldPreventStreamClosedException() throws IOException {
        // Given
        String responseContent = "{\"test\": \"data\", \"large_field\": \"" + "x".repeat(10000) + "\"}";

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/stream-test")
                .respond()
                .withStatus(200)
                .withBody(responseContent);

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/stream-test"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // Then - Multiple reads should not cause StreamClosedException
        InputStream body1 = response.getBody();
        assertNotNull(body1);

        // Read part of the stream
        byte[] buffer = new byte[1024];
        int bytesRead = body1.read(buffer);
        assertTrue(bytesRead > 0);

        // Get body again - this should work with our implementation
        InputStream body2 = response.getBody();
        assertNotNull(body2);

        // Clean close should not throw StreamClosedException
        response.close();
    }

    @Test
    void shouldHandleConcurrentRequests() throws Exception {
        // Given
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/concurrent")
                .respond()
                .withStatus(200)
                .withBody("{\"thread\": \"response\"}");

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // When - Execute 20 concurrent requests
        @SuppressWarnings("unchecked")
        CompletableFuture<Boolean>[] futures = new CompletableFuture[20];
        for (int i = 0; i < 20; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    ClientHttpRequest request = factory.createRequest(
                            URI.create(baseUrl + "/api/concurrent"), HttpMethod.GET);
                    ClientHttpResponse response = request.execute();

                    boolean success = response.getStatusCode().value() == 200;
                    response.close();
                    return success;
                } catch (Exception e) {
                    return false;
                }
            }, executor);
        }

        // Then - All requests should succeed
        for (CompletableFuture<Boolean> future : futures) {
            assertTrue(future.get(10, TimeUnit.SECONDS));
        }

        executor.shutdown();
    }

    @Test
    void shouldHandleConnectionRefused() {
        // Given - Valid port that should refuse connection (assuming nothing runs on 54321)
        HttpClient5ComponentsClientHttpRequestFactory invalidFactory =
                new HttpClient5ComponentsClientHttpRequestFactory(httpClient);

        // When & Then
        assertThrows(IOException.class, () -> {
            ClientHttpRequest request = invalidFactory.createRequest(
                    URI.create("http://localhost:54321/nonexistent"), HttpMethod.GET);
            request.execute();
        });
    }

    @Test
    void shouldHandleInvalidUrl() throws IOException {
        // Given
        ClientHttpRequest request = factory.createRequest(
                URI.create("http://invalid-host-that-does-not-exist.invalid/api"), HttpMethod.GET);

        // When & Then
        assertThrows(IOException.class, () -> request.execute());
    }

    @Test
    void shouldHandleMultipleCloseCallsGracefully() throws IOException {
        // Given
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/close-test")
                .respond()
                .withStatus(200)
                .withBody("{\"test\": \"close\"}");

        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/close-test"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        // When & Then - Multiple closes should not throw exceptions
        response.close();
        response.close(); // Should not throw
        response.close(); // Should not throw

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void shouldHandleResponseBodyAfterPartialRead() throws IOException {
        // Given
        String longContent = "START:" + "x".repeat(50000) + ":END";

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/api/partial-read")
                .respond()
                .withStatus(200)
                .withBody(longContent);

        // When
        ClientHttpRequest request = factory.createRequest(
                URI.create(baseUrl + "/api/partial-read"), HttpMethod.GET);
        ClientHttpResponse response = request.execute();

        InputStream body = response.getBody();

        // Read only first 100 bytes
        byte[] buffer = new byte[100];
        int bytesRead = body.read(buffer);
        assertEquals(100, bytesRead);

        String partialContent = new String(buffer, StandardCharsets.UTF_8);
        assertTrue(partialContent.startsWith("START:"));

        // Then - Close should handle remaining content gracefully
        response.close(); // Should not throw StreamClosedException

        assertEquals(200, response.getStatusCode().value());
    }

    /**
     * Helper method to read response body as string
     */
    private String readResponseBody(ClientHttpResponse response) throws IOException {
        try (InputStream inputStream = response.getBody()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8);
        }
    }
}