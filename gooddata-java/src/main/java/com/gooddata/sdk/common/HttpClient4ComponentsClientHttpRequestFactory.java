/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;


import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
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
 * Spring 6 compatible {@link ClientHttpRequestFactory} implementation that uses Apache HttpComponents HttpClient 4.x.
 * This is a custom implementation to bridge the gap between Spring 6 (which expects HttpClient 5.x) 
 * and our requirement to use HttpClient 4.x for compatibility.
 */
public class HttpClient4ComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient4ComponentsClientHttpRequestFactory.class);
    private final HttpClient httpClient;

    /**
     * Create a factory with the given HttpClient 4.x instance.
     * 
     * @param httpClient the HttpClient 4.x instance to use
     */
    public HttpClient4ComponentsClientHttpRequestFactory(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        return new HttpClient4ComponentsClientHttpRequest(httpClient, httpRequest);
    }

    /**
     * Create an Apache HttpComponents HttpUriRequest object for the given HTTP method and URI.
     * 
     * @param httpMethod the HTTP method
     * @param uri the URI
     * @return the HttpUriRequest
     */
    private HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        if (HttpMethod.GET.equals(httpMethod)) {
            return new HttpGet(uri);
        } else if (HttpMethod.HEAD.equals(httpMethod)) {
            return new HttpHead(uri);
        } else if (HttpMethod.POST.equals(httpMethod)) {
            return new HttpPost(uri);
        } else if (HttpMethod.PUT.equals(httpMethod)) {
            return new HttpPut(uri);
        } else if (HttpMethod.PATCH.equals(httpMethod)) {
            return new HttpPatch(uri);
        } else if (HttpMethod.DELETE.equals(httpMethod)) {
            return new HttpDelete(uri);
        } else if (HttpMethod.OPTIONS.equals(httpMethod)) {
            return new HttpOptions(uri);
        } else if (HttpMethod.TRACE.equals(httpMethod)) {
            return new HttpTrace(uri);
        } else {
            throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    /**
     * {@link ClientHttpRequest} implementation based on Apache HttpComponents HttpClient 4.x.
     */
    private static class HttpClient4ComponentsClientHttpRequest implements ClientHttpRequest {

        private final HttpClient httpClient;
        private final HttpUriRequest httpRequest;
        private final HttpHeaders headers;
        private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

        public HttpClient4ComponentsClientHttpRequest(HttpClient httpClient, HttpUriRequest httpRequest) {
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
            return httpRequest.getURI();
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
                if (httpRequest instanceof HttpEntityEnclosingRequest) {
                    HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) httpRequest;
                    
                    // Ensure proper UTF-8 encoding before creating entity
                    // This is crucial for @JsonTypeInfo annotated classes like Execution
                    ByteArrayEntity requestEntity = new ByteArrayEntity(bytes);
                    

                    if (logger.isDebugEnabled()) {
                        // Check if Content-Type is already set in headers
                        boolean hasContentType = false;
                        for (org.apache.http.Header header : httpRequest.getAllHeaders()) {
                            if ("Content-Type".equalsIgnoreCase(header.getName())) {
                                hasContentType = true;
                                // String contentType = header.getValue();
                                // logger.debug("Content-Type from headers: {}", contentType);
                                break;
                            }
                        }
                        
                        if (!hasContentType) {
                            // logger.debug("Default Content-Type set: application/json; charset=UTF-8");
                        }
                    }
                    
                    entityRequest.setEntity(requestEntity);
                    
                }
            }
            
            // Set headers exactly like reference implementation
            // (no additional headers parameter in our case, but same logic)
            addHeaders(httpRequest);
            
            // Handle both GoodDataHttpClient and standard HttpClient
            org.apache.http.HttpResponse httpResponse;
            if (httpClient.getClass().getName().contains("GoodDataHttpClient")) {
                // Use reflection to call the execute method on GoodDataHttpClient
                try {
                    // Try the single parameter execute method first
                    java.lang.reflect.Method executeMethod = httpClient.getClass().getMethod("execute", 
                        org.apache.http.client.methods.HttpUriRequest.class);
                    httpResponse = (org.apache.http.HttpResponse) executeMethod.invoke(httpClient, httpRequest);
                } catch (NoSuchMethodException e) {
                    // If that doesn't work, try the two parameter version with HttpContext
                    try {
                        java.lang.reflect.Method executeMethod = httpClient.getClass().getMethod("execute", 
                            org.apache.http.client.methods.HttpUriRequest.class, org.apache.http.protocol.HttpContext.class);
                        httpResponse = (org.apache.http.HttpResponse) executeMethod.invoke(httpClient, httpRequest, null);
                    } catch (Exception e2) {
                        throw new IOException("Failed to execute request with GoodDataHttpClient", e2);
                    }
                } catch (Exception e) {
                    throw new IOException("Failed to execute request with GoodDataHttpClient", e);
                }
            } else {
                httpResponse = httpClient.execute(httpRequest);
            }
            return new HttpClient4ComponentsClientHttpResponse(httpResponse);
        }

        /**
         * Add the headers from the HttpHeaders to the HttpRequest.
         * Excludes Content-Length headers to avoid conflicts with HttpClient 4.x internal management.
         * Uses setHeader instead of addHeader to match the reference implementation.
         * Follows HttpClient4ClientHttpRequest.executeInternal implementation pattern.
         */
        private void addHeaders(HttpRequest httpRequest) {
            // CRITICAL for GoodData API: set headers in fixed order
            // for stable checksum. Order: Accept, X-GDC-Version, Content-Type, others
            
            // First clear potentially problematic headers
            if (httpRequest instanceof HttpUriRequest) {
                HttpUriRequest uriRequest = (HttpUriRequest) httpRequest;
                uriRequest.removeHeaders("Accept");
                uriRequest.removeHeaders("X-GDC-Version");
                uriRequest.removeHeaders("Content-Type");
            }
            
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
            } else if (httpRequest instanceof HttpEntityEnclosingRequest) {
                // Set default Content-Type for JSON requests with body
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
