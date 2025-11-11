/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;


import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.client5.http.classic.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Spring 6 compatible {@link ClientHttpRequestFactory} implementation that uses Apache HttpComponents HttpClient 5.x.
 * This is a custom implementation to bridge the gap between Spring 6 and HttpClient 5.x.
 */
public class HttpClient4ComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient4ComponentsClientHttpRequestFactory.class);
    private final HttpClient httpClient;

    /**
     * Create a factory with the given HttpClient 5.x instance.
     * 
     * @param httpClient the HttpClient 5.x instance to use
     */
    public HttpClient4ComponentsClientHttpRequestFactory(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        ClassicHttpRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        return new HttpClient4ComponentsClientHttpRequest(httpClient, httpRequest);
    }

    /**
     * Create an Apache HttpComponents ClassicHttpRequest object for the given HTTP method and URI.
     * 
     * @param httpMethod the HTTP method
     * @param uri the URI
     * @return the ClassicHttpRequest
     */
    private ClassicHttpRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        if (HttpMethod.GET.equals(httpMethod)) {
            return ClassicRequestBuilder.get(uri).build();
        } else if (HttpMethod.HEAD.equals(httpMethod)) {
            return ClassicRequestBuilder.head(uri).build();
        } else if (HttpMethod.POST.equals(httpMethod)) {
            return ClassicRequestBuilder.post(uri).build();
        } else if (HttpMethod.PUT.equals(httpMethod)) {
            return ClassicRequestBuilder.put(uri).build();
        } else if (HttpMethod.PATCH.equals(httpMethod)) {
            return ClassicRequestBuilder.patch(uri).build();
        } else if (HttpMethod.DELETE.equals(httpMethod)) {
            return ClassicRequestBuilder.delete(uri).build();
        } else if (HttpMethod.OPTIONS.equals(httpMethod)) {
            return ClassicRequestBuilder.options(uri).build();
        } else if (HttpMethod.TRACE.equals(httpMethod)) {
            return ClassicRequestBuilder.trace(uri).build();
        } else {
            throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    /**
     * {@link ClientHttpRequest} implementation based on Apache HttpComponents HttpClient 5.x.
     */
    private static class HttpClient4ComponentsClientHttpRequest implements ClientHttpRequest {

        private final HttpClient httpClient;
        private final ClassicHttpRequest httpRequest;
        private final HttpHeaders headers;
        private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

        public HttpClient4ComponentsClientHttpRequest(HttpClient httpClient, ClassicHttpRequest httpRequest) {
            this.httpClient = httpClient;
            this.httpRequest = httpRequest;
            this.headers = new HttpHeaders();
        }

        @Override
        public HttpMethod getMethod() {
            return HttpMethod.valueOf(httpRequest.getMethod());
        }

        @Override
        public String getMethodValue() {
            return httpRequest.getMethod();
        }

        @Override
        public URI getURI() {
            try {
                return httpRequest.getUri();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get URI", e);
            }
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return bufferedOutput;
        }

        @Override
        public ClientHttpResponse execute() throws IOException {
            // Create entity first (matching reference implementation exactly)
            byte[] bytes = bufferedOutput.toByteArray();
            if (bytes.length > 0) {
                // HttpClient 5.x - set entity directly on the request
                ByteArrayEntity requestEntity = new ByteArrayEntity(bytes, null);
                httpRequest.setEntity(requestEntity);
            }
            
            // Set headers exactly like reference implementation
            addHeaders(httpRequest);
            
            // Extract HttpHost from the request URI for GoodDataHttpClient
            // GoodDataHttpClient requires the target host to be explicitly provided
            // to properly handle authentication and token management
            try {
                URI requestUri = httpRequest.getUri();
                org.apache.hc.core5.http.HttpHost target = new org.apache.hc.core5.http.HttpHost(
                    requestUri.getScheme(),
                    requestUri.getHost(),
                    requestUri.getPort()
                );
                
                // CRITICAL: Call execute() WITHOUT ResponseHandler to ensure GoodDataHttpClient's
                // authentication logic is invoked. The version with ResponseHandler bypasses auth!
                // See: gooddata-http-client:2.0.0 GoodDataHttpClient.execute() implementation
                ClassicHttpResponse response = httpClient.execute(target, httpRequest);
                
                // We need to consume and store the response immediately since the connection may be closed
                return new HttpClient4ComponentsClientHttpResponse(response);
            } catch (java.net.URISyntaxException e) {
                throw new IOException("Failed to extract target host from request URI", e);
            }
        }

        /**
         * Add the headers from the HttpHeaders to the HttpRequest.
         * Excludes Content-Length headers to avoid conflicts with HttpClient 5.x internal management.
         * Uses setHeader instead of addHeader to match the reference implementation.
         */
        private void addHeaders(ClassicHttpRequest httpRequest) {
            // CRITICAL for GoodData API: set headers in fixed order
            // for stable checksum. Order: Accept, X-GDC-Version, Content-Type, others
            
            // First clear potentially problematic headers
            httpRequest.removeHeaders("Accept");
            httpRequest.removeHeaders("X-GDC-Version");
            httpRequest.removeHeaders("Content-Type");
            
            // 1. Accept header (first for checksum stability)
            if (headers.containsKey("Accept")) {
                String acceptValue = String.join(", ", headers.get("Accept"));
                httpRequest.setHeader("Accept", acceptValue);
                // if (logger.isDebugEnabled()) {
                //     logger.debug("Header: Accept = {}", acceptValue);
                // }
            }
            
            // 2. X-GDC-Version header (second for stability)
            if (headers.containsKey("X-GDC-Version")) {
                String versionValue = String.join(", ", headers.get("X-GDC-Version"));
                httpRequest.setHeader("X-GDC-Version", versionValue);
                // if (logger.isDebugEnabled()) {
                //     logger.debug("Header: X-GDC-Version = {}", versionValue);
                // }
            }
            
            // 3. Content-Type - managed only through headers (without entity Content-Type)
            String finalContentType = null;
            if (headers.containsKey("Content-Type")) {
                // Use Spring Content-Type header
                String contentTypeValue = String.join(", ", headers.get("Content-Type"));
                // Add charset=UTF-8 for JSON if not present
                if (contentTypeValue.contains("application/json") && !contentTypeValue.contains("charset=")) {
                    finalContentType = contentTypeValue + "; charset=UTF-8";
                    // if (logger.isDebugEnabled()) {
                    //     logger.debug("Enhanced Content-Type for JSON: {}", finalContentType);
                    // }
                } else {
                    finalContentType = contentTypeValue;
                    // if (logger.isDebugEnabled()) {
                    //     logger.debug("Using Spring Content-Type: {}", finalContentType);
                    // }
                }
            } else {
                // Set default Content-Type for JSON requests with body
                // In HttpClient 5.x, all requests can have entities, no need for instanceof check
                finalContentType = "application/json; charset=UTF-8";
                // if (logger.isDebugEnabled()) {
                //     logger.debug("Default Content-Type for JSON requests: {}", finalContentType);
                // }
            }
            
            if (finalContentType != null) {
                httpRequest.setHeader("Content-Type", finalContentType);
                // if (logger.isDebugEnabled()) {
                //     logger.debug("Header: Content-Type = {}", finalContentType);
                // }
            }
            
            // 4. All other headers (in alphabetical order for stability)
            headers.entrySet().stream()
                .filter(entry -> {
                    String headerName = entry.getKey();
                    return !"Content-Length".equalsIgnoreCase(headerName) && 
                           !"Transfer-Encoding".equalsIgnoreCase(headerName) &&
                           !"Content-Type".equalsIgnoreCase(headerName) &&
                           !"Accept".equalsIgnoreCase(headerName) &&
                           !"X-GDC-Version".equalsIgnoreCase(headerName);
                })
                .sorted(Map.Entry.comparingByKey()) // Alphabetical order for stability
                .forEach(entry -> {
                    String headerName = entry.getKey();
                    List<String> headerValues = entry.getValue();
                    
                    String headerValue;
                    if ("Cookie".equalsIgnoreCase(headerName)) {  // RFC 6265
                        headerValue = String.join("; ", headerValues);
                    } else {
                        headerValue = String.join(", ", headerValues);
                    }
                    
                    httpRequest.setHeader(headerName, headerValue);
                    // if (logger.isDebugEnabled()) {
                    //     logger.debug("Header: {} = {}", headerName, headerValue);
                    // }
                });
            
            // Log final headers state for checksum debugging
            // if (logger.isDebugEnabled()) {
            //     org.apache.http.Header[] allHeaders = httpRequest.getAllHeaders();
            //     logger.debug("Final request headers count: {}", allHeaders.length);
            //     for (org.apache.http.Header header : allHeaders) {
            //         logger.debug("Final header: {} = {}", header.getName(), header.getValue());
            //     }
            // }
        }
    }
}
