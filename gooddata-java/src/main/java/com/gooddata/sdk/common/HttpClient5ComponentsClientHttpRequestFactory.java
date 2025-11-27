/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Spring 6 compatible {@link ClientHttpRequestFactory} implementation that uses Apache HttpComponents HttpClient 5.x Classic API.
 * This implementation bridges the gap between Spring 6 and HttpClient 5.x, supporting the Classic API pattern
 * used by gooddata-http-client.
 */
public class HttpClient5ComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient5ComponentsClientHttpRequestFactory.class);
    private final HttpClient httpClient;

    /**
     * Create a factory with the given HttpClient 5.x instance.
     *
     * @param httpClient the HttpClient 5.x instance to use
     */
    public HttpClient5ComponentsClientHttpRequestFactory(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        ClassicHttpRequest httpRequest = createHttpRequest(httpMethod, uri);
        return new HttpClient5ComponentsClientHttpRequest(httpClient, httpRequest);
    }

    /**
     * Create an Apache HttpComponents ClassicHttpRequest object for the given HTTP method and URI.
     *
     * @param httpMethod the HTTP method
     * @param uri        the URI
     * @return the ClassicHttpRequest
     */
    private ClassicHttpRequest createHttpRequest(HttpMethod httpMethod, URI uri) {
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
     * {@link ClientHttpRequest} implementation based on Apache HttpComponents HttpClient 5.x Classic API.
     */
    private static class HttpClient5ComponentsClientHttpRequest implements ClientHttpRequest {

        private final HttpClient httpClient;
        private final ClassicHttpRequest httpRequest;
        private final HttpHeaders headers;
        private final ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

        public HttpClient5ComponentsClientHttpRequest(HttpClient httpClient, ClassicHttpRequest httpRequest) {
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
                throw new RuntimeException("Failed to get URI from request", e);
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
                if (httpRequest != null) {

                    // Ensure proper UTF-8 encoding before creating entity
                    // This is crucial for @JsonTypeInfo annotated classes like Execution
                    ContentType contentType = ContentType.APPLICATION_JSON;

                    if (logger.isDebugEnabled()) {
                        // Check if Content-Type is already set in headers
                        boolean hasContentType = false;
                        for (org.apache.hc.core5.http.Header header : httpRequest.getHeaders()) {
                            if ("Content-Type".equalsIgnoreCase(header.getName())) {
                                hasContentType = true;
                                contentType = ContentType.parse(header.getValue());
                                break;
                            }
                        }

                        if (!hasContentType) {
                            logger.debug("No Content-Type header found, using application/json as default");
                        }
                    }

                    ByteArrayEntity requestEntity = new ByteArrayEntity(bytes, contentType);
                    ((HttpEntityContainer) httpRequest).setEntity(requestEntity);
                }
            }

            // Add headers to request, excluding Content-Length as HttpClient 5.x manages it internally
            addHeaders(httpRequest);

            // Execute request
            ClassicHttpResponse httpResponse;
            if (httpClient.getClass().getName().contains("GoodDataHttpClient")) {
                // Use reflection to call the execute method on GoodDataHttpClient
                try {
                    // Try the single parameter execute method first
                    java.lang.reflect.Method executeMethod = httpClient.getClass().getMethod("execute",
                            ClassicHttpRequest.class);
                    httpResponse = (ClassicHttpResponse) executeMethod.invoke(httpClient, httpRequest);
                } catch (NoSuchMethodException e) {
                    // If that doesn't work, try the two parameter version with HttpContext
                    try {
                        java.lang.reflect.Method executeMethod = httpClient.getClass().getMethod("execute",
                                ClassicHttpRequest.class, HttpContext.class);
                        httpResponse = (ClassicHttpResponse) executeMethod.invoke(httpClient, httpRequest, null);
                    } catch (Exception e2) {
                        throw new IOException("Failed to execute request with GoodDataHttpClient", e2);
                    }
                } catch (Exception e) {
                    throw new IOException("Failed to execute request with GoodDataHttpClient", e);
                }
            } else {
                httpResponse = httpClient.execute(httpRequest, (ClassicHttpResponse response) -> response);
            }
            return new HttpClient5ComponentsClientHttpResponse(httpResponse);
        }

        /**
         * Add the headers from the HttpHeaders to the HttpRequest.
         * Excludes Content-Length headers to avoid conflicts with HttpClient 5.x internal management.
         * Uses setHeader instead of addHeader to match the reference implementation.
         * Follows HttpClient5 Classic API patterns.
         */
        private void addHeaders(ClassicHttpRequest httpRequest) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String headerName = entry.getKey();
                // Skip Content-Length as HttpClient 5.x manages it internally
                if (!"Content-Length".equalsIgnoreCase(headerName)) {
                    for (String headerValue : entry.getValue()) {
                        httpRequest.setHeader(headerName, headerValue);
                    }
                }
            }
        }
    }

    /**
     * {@link ClientHttpResponse} implementation based on Apache HttpComponents HttpClient 5.x Classic API.
     */
    private static class HttpClient5ComponentsClientHttpResponse implements ClientHttpResponse {

        private final ClassicHttpResponse httpResponse;
        private HttpHeaders headers;

        public HttpClient5ComponentsClientHttpResponse(ClassicHttpResponse httpResponse) {
            this.httpResponse = httpResponse;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return HttpStatusCode.valueOf(httpResponse.getCode());
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return httpResponse.getCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return httpResponse.getReasonPhrase();
        }

        @Override
        public HttpHeaders getHeaders() {
            if (this.headers == null) {
                this.headers = new HttpHeaders();
                for (org.apache.hc.core5.http.Header header : httpResponse.getHeaders()) {
                    this.headers.add(header.getName(), header.getValue());
                }
            }
            return this.headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? entity.getContent() : InputStream.nullInputStream();
        }

        @Override
        public void close() {
            try {
                // Properly close the response to return connection to pool
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                // Log the error but don't throw - closing should be best effort
                // This ensures connections are returned to the pool
            }
        }
    }
}