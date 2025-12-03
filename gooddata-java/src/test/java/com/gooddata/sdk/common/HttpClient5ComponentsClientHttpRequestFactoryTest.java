/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test for {@link HttpClient5ComponentsClientHttpRequestFactory}
 */
class HttpClient5ComponentsClientHttpRequestFactoryTest {

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
}