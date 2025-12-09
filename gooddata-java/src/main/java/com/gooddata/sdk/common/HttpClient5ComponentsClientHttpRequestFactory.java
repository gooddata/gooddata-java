/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import com.gooddata.http.client.GoodDataHttpClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
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
 * Spring {@link ClientHttpRequestFactory} backed by Apache HttpClient 5 Classic API.
 */
public class HttpClient5ComponentsClientHttpRequestFactory implements ClientHttpRequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient5ComponentsClientHttpRequestFactory.class);

    private final HttpClient httpClient;

    public HttpClient5ComponentsClientHttpRequestFactory(final HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    @Override
    public ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        final ClassicHttpRequest httpRequest = createHttpRequest(httpMethod, uri);
        return new HttpClient5ComponentsClientHttpRequest(httpClient, httpRequest);
    }

    private ClassicHttpRequest createHttpRequest(final HttpMethod httpMethod, final URI uri) {
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
     * {@link ClientHttpRequest} implementation using Apache HttpClient 5 Classic API.
     */
    private static class HttpClient5ComponentsClientHttpRequest implements ClientHttpRequest {

        private final HttpClient httpClient;
        private final ClassicHttpRequest httpRequest;
        private final HttpHeaders headers;
        private final ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

        HttpClient5ComponentsClientHttpRequest(final HttpClient httpClient, final ClassicHttpRequest httpRequest) {
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
        public OutputStream getBody() {
            return bufferedOutput;
        }

        @Override
        public ClientHttpResponse execute() throws IOException {
            final byte[] bytes = bufferedOutput.toByteArray();
            if (bytes.length > 0 && httpRequest instanceof HttpEntityContainer) {
                // Determine content type from Spring headers first, then fallback to default
                ContentType contentType = ContentType.APPLICATION_JSON;
                final String contentTypeHeader = headers.getFirst(org.springframework.http.HttpHeaders.CONTENT_TYPE);
                if (contentTypeHeader != null) {
                    try {
                        contentType = ContentType.parse(contentTypeHeader);
                    } catch (Exception e) {
                        logger.warn("Failed to parse Content-Type header '{}', using default", contentTypeHeader, e);
                    }
                }

                final ByteArrayEntity requestEntity = new ByteArrayEntity(bytes, contentType);
                ((HttpEntityContainer) httpRequest).setEntity(requestEntity);
            }

            // Add headers after setting entity to avoid conflicts
            addHeaders(httpRequest);

            final ClassicHttpResponse httpResponse = executeClassic(httpClient, httpRequest);
            return new HttpClient5ComponentsClientHttpResponse(httpResponse);
        }

        /**
         * Execute the request while keeping the response stream open for callers (e.g. RestTemplate)
         * to consume. Avoids using response handlers which auto-close streams.
         *
         * <p>GoodDataHttpClient provides direct execute() methods that return ClassicHttpResponse
         * without auto-closing streams. For standard CloseableHttpClient, we use executeOpen().</p>
         */
        private ClassicHttpResponse executeClassic(final HttpClient httpClient,
                                                   final ClassicHttpRequest httpRequest) throws IOException {
            if (logger.isDebugEnabled()) {
                try {
                    logger.debug("Executing request: {} {}", httpRequest.getMethod(), httpRequest.getUri());
                } catch (Exception e) {
                    logger.debug("Executing request: {}", httpRequest.getMethod());
                }
            }

            // GoodDataHttpClient has direct execute() that returns ClassicHttpResponse
            // without auto-closing streams - preferred for authenticated GoodData API calls
            if (httpClient instanceof GoodDataHttpClient) {
                logger.debug("Using GoodDataHttpClient execute");
                return ((GoodDataHttpClient) httpClient).execute(httpRequest);
            }

            // For CloseableHttpClient (and subclasses), use executeOpen to keep streams alive
            if (httpClient instanceof CloseableHttpClient) {
                logger.debug("Using CloseableHttpClient executeOpen");
                return ((CloseableHttpClient) httpClient).executeOpen(null, httpRequest, null);
            }

            // Fallback for other HttpClient implementations
            logger.debug("Using standard HttpClient executeOpen");
            return httpClient.executeOpen(null, httpRequest, null);
        }

        /**
         * Add the headers from the HttpHeaders to the HttpRequest.
         * Excludes headers that HttpClient 5 manages internally or that were already set.
         */
        private void addHeaders(final ClassicHttpRequest httpRequest) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                final String headerName = entry.getKey();

                // Skip headers that HttpClient manages internally or that would cause conflicts
                if ("Content-Length".equalsIgnoreCase(headerName) ||
                    "Transfer-Encoding".equalsIgnoreCase(headerName) ||
                    "Connection".equalsIgnoreCase(headerName) ||
                    "Host".equalsIgnoreCase(headerName)) {
                    continue;
                }

                // Remove any existing headers with the same name first to avoid duplicates
                httpRequest.removeHeaders(headerName);

                // Add all values for this header
                for (String headerValue : entry.getValue()) {
                    if (headerValue != null) {
                        httpRequest.addHeader(headerName, headerValue);
                    }
                }
            }
        }
    }

    /**
     * {@link ClientHttpResponse} implementation backed by Apache HttpClient 5 Classic API.
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
            if (headers == null) {
                headers = new HttpHeaders();
                for (org.apache.hc.core5.http.Header header : httpResponse.getHeaders()) {
                    headers.add(header.getName(), header.getValue());
                }
            }
            return headers;
        }

        @Override
        public InputStream getBody() throws IOException {
            final HttpEntity entity = httpResponse.getEntity();
            if (entity == null) {
                logger.debug("No entity in response, returning empty stream");
                return InputStream.nullInputStream();
            }

            try {
                final InputStream content = entity.getContent();
                if (content == null) {
                    logger.debug("Entity content is null, returning empty stream");
                    return InputStream.nullInputStream();
                }

                // Log entity details for debugging
                if (logger.isTraceEnabled()) {
                    logger.trace("Returning entity content stream - repeatable: {}, streaming: {}, content length: {}",
                               entity.isRepeatable(), entity.isStreaming(), entity.getContentLength());
                }

                return content;
            } catch (IllegalStateException e) {
                logger.warn("Entity content stream is in illegal state (likely already consumed): {}", e.getMessage());
                throw new IOException("Response stream is no longer available: " + e.getMessage(), e);
            }
        }

        @Override
        public void close() {
            try {
                logger.debug("Closing HTTP response");
                if (httpResponse != null) {
                    // For Apache HttpClient 5, we should just close the response directly
                    // The connection manager handles proper connection cleanup automatically
                    httpResponse.close();
                }
            } catch (IOException e) {
                logger.debug("Unable to close HTTP response", e);
            }
        }
    }
}
