/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

/**
 * Comprehensive unit tests for {@link HttpClient5ComponentsClientHttpRequestFactory}
 */
class HttpClient5ComponentsClientHttpRequestFactoryTest {

    private HttpClient mockHttpClient;
    private CloseableHttpClient mockCloseableHttpClient;
    private HttpClient5ComponentsClientHttpRequestFactory factory;
    private ClassicHttpResponse mockResponse;

    @BeforeMethod
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockCloseableHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(ClassicHttpResponse.class);
        factory = new HttpClient5ComponentsClientHttpRequestFactory(mockHttpClient);
    }

    @Test
    void shouldCreateRequestWithHttpClient5() throws Exception {
        // Given
        HttpClient httpClient = HttpClients.createDefault();
        HttpClient5ComponentsClientHttpRequestFactory factory = new HttpClient5ComponentsClientHttpRequestFactory(httpClient);

        // When
        ClientHttpRequest request = factory.createRequest(URI.create("https://example.com/api"), HttpMethod.GET);

        // Then
        assertNotNull(request);
        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(URI.create("https://example.com/api"), request.getURI());
    }

    @Test
    void shouldHandleAllHttpMethods() throws Exception {
        // Given
        HttpClient httpClient = HttpClients.createDefault();
        HttpClient5ComponentsClientHttpRequestFactory factory = new HttpClient5ComponentsClientHttpRequestFactory(httpClient);
        URI testUri = URI.create("https://example.com/api");

        // Test all standard HTTP methods
        HttpMethod[] methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS,
                HttpMethod.PATCH, HttpMethod.TRACE};

        for (HttpMethod method : methods) {
            // When
            ClientHttpRequest request = factory.createRequest(testUri, method);

            // Then
            assertNotNull(request, "Request should not be null for method: " + method);
            assertEquals(method, request.getMethod(), "Method should match for: " + method);
            assertEquals(testUri, request.getURI(), "URI should match for method: " + method);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    void shouldRequireNonNullHttpClient() {
        // When & Then
        new HttpClient5ComponentsClientHttpRequestFactory(null);
    }

    @Test
    public void shouldThrowExceptionForInvalidHttpMethod() throws Exception {
        // Given
        HttpClient httpClient = HttpClients.createDefault();
        HttpClient5ComponentsClientHttpRequestFactory factory = new HttpClient5ComponentsClientHttpRequestFactory(httpClient);

        // When & Then - Test that creating request with invalid method throws exception
        try {
            factory.createRequest(URI.create("http://test.com"), HttpMethod.valueOf("INVALID"));
            assertTrue(false, "Should have thrown exception for invalid HTTP method");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid HTTP method"));
        }
    }

    @Test
    void shouldHandleRequestExecution() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getReasonPhrase()).thenReturn("OK");
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("OK", response.getStatusText());
    }

    @Test
    void shouldHandleRequestBodyAndHeaders() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.POST);
        
        // When
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        request.getHeaders().add("Custom-Header", "test-value");
        request.getBody().write("{\"test\": \"data\"}".getBytes());

        // Then
        assertNotNull(request.getHeaders());
        assertEquals(MediaType.APPLICATION_JSON, request.getHeaders().getContentType());
        assertTrue(request.getHeaders().containsKey("Custom-Header"));
        
        ClientHttpResponse response = request.execute();
        assertNotNull(response);
    }

    @Test
    void shouldHandleResponseWithEntity() throws Exception {
        // Given
        HttpEntity mockEntity = mock(HttpEntity.class);
        InputStream testStream = new ByteArrayInputStream("test response".getBytes());
        
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContent()).thenReturn(testStream);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response.getBody());
        assertTrue(response.getBody().available() > 0);
    }

    @Test
    void shouldHandleResponseWithoutEntity() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(204);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);
        when(mockResponse.getEntity()).thenReturn(null);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.DELETE);

        // When
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().available());
    }

    @Test
    void shouldHandleCloseableHttpClient() throws Exception {
        // Given
        factory = new HttpClient5ComponentsClientHttpRequestFactory(mockCloseableHttpClient);
        when(mockCloseableHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response);
        verify(mockCloseableHttpClient).executeOpen(isNull(), any(ClassicHttpRequest.class), isNull());
    }

    @Test
    void shouldSkipSystemManagedHeaders() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.POST);
        
        // When - Add headers that should be skipped
        request.getHeaders().add("Content-Length", "123");
        request.getHeaders().add("Transfer-Encoding", "chunked");
        request.getHeaders().add("Connection", "keep-alive");
        request.getHeaders().add("Host", "test.com");
        request.getHeaders().add("Custom-Header", "should-be-added");

        ArgumentCaptor<ClassicHttpRequest> requestCaptor = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response);
        verify(mockHttpClient).executeOpen(isNull(), requestCaptor.capture(), isNull());
        ClassicHttpRequest capturedRequest = requestCaptor.getValue();
        
        // System managed headers should not be present
        assertFalse(capturedRequest.containsHeader("Content-Length"));
        assertFalse(capturedRequest.containsHeader("Transfer-Encoding"));
        assertFalse(capturedRequest.containsHeader("Connection"));
        assertFalse(capturedRequest.containsHeader("Host"));
        
        // Custom header should be present
        assertTrue(capturedRequest.containsHeader("Custom-Header"));
    }

    @Test
    void shouldHandleContentTypeFromHeaders() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.POST);
        
        // When
        request.getHeaders().setContentType(MediaType.APPLICATION_XML);
        request.getBody().write("<xml>test</xml>".getBytes());

        ArgumentCaptor<ClassicHttpRequest> requestCaptor = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        ClientHttpResponse response = request.execute();

        // Then
        assertNotNull(response);
        verify(mockHttpClient).executeOpen(isNull(), requestCaptor.capture(), isNull());
        ClassicHttpRequest capturedRequest = requestCaptor.getValue();
        
        if (capturedRequest instanceof HttpPost) {
            HttpPost postRequest = (HttpPost) capturedRequest;
            HttpEntity entity = postRequest.getEntity();
            assertNotNull(entity);
            assertEquals(ContentType.APPLICATION_XML.getMimeType(), 
                        entity.getContentType().split(";")[0]);
        }
    }

    @Test
    void shouldHandleInvalidContentType() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.POST);
        
        // When - Set invalid content type
        request.getHeaders().add("Content-Type", "invalid/content-type/malformed");
        request.getBody().write("test".getBytes());

        // Then - Should not throw exception, should use default
        ClientHttpResponse response = request.execute();
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void shouldHandleResponseClose() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When
        ClientHttpResponse response = request.execute();
        response.close();

        // Then
        verify(mockResponse).close();
    }

    @Test
    void shouldHandleResponseCloseException() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenReturn(mockResponse);
        when(mockResponse.getCode()).thenReturn(200);
        when(mockResponse.getHeaders()).thenReturn(new org.apache.hc.core5.http.Header[0]);
        doThrow(new IOException("Close failed")).when(mockResponse).close();
        
        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When
        ClientHttpResponse response = request.execute();
        
        // Then - Should not throw exception
        response.close(); // Should handle IOException gracefully
    }

    @Test
    void shouldHandleExecutionException() throws Exception {
        // Given
        when(mockHttpClient.executeOpen(isNull(), any(ClassicHttpRequest.class), isNull()))
                .thenThrow(new IOException("Connection failed"));

        ClientHttpRequest request = factory.createRequest(URI.create("http://test.com"), HttpMethod.GET);

        // When & Then
        assertThrows(IOException.class, () -> request.execute());
    }
}