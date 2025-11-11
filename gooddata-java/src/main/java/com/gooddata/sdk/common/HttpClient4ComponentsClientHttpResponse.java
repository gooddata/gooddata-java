/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Spring 6 compatible {@link ClientHttpResponse} implementation that wraps Apache HttpComponents HttpClient 5.x response.
 * This bridges HttpClient 5.x responses with Spring 6's ClientHttpResponse interface.
 * 
 * <p>IMPORTANT: This class buffers the entire response body in memory to avoid issues with
 * HttpClient 5.x's ResponseHandler automatically closing the response stream. This is necessary
 * because Spring 6's IntrospectingClientHttpResponse needs to read the stream after the
 * ResponseHandler has returned.
 * 
 * Package-private as it's only used internally within the common package.
 */
class HttpClient4ComponentsClientHttpResponse implements ClientHttpResponse {

    private final int statusCode;
    private final String reasonPhrase;
    private final HttpHeaders headers;
    private final byte[] bodyBytes;

    /**
     * Creates a response wrapper that immediately buffers the response body.
     * This must be called within the ResponseHandler before the response is closed.
     * 
     * @param httpResponse the HttpClient 5.x response to wrap
     * @throws IOException if reading the response body fails
     */
    public HttpClient4ComponentsClientHttpResponse(ClassicHttpResponse httpResponse) throws IOException {
        // Capture response metadata immediately
        this.statusCode = httpResponse.getCode();
        this.reasonPhrase = httpResponse.getReasonPhrase();
        
        // Copy headers
        this.headers = new HttpHeaders();
        for (Header header : httpResponse.getHeaders()) {
            this.headers.add(header.getName(), header.getValue());
        }
        
        // Buffer the response body BEFORE the ResponseHandler closes the stream
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            try (InputStream content = entity.getContent()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] chunk = new byte[8192];
                int bytesRead;
                while ((bytesRead = content.read(chunk)) != -1) {
                    buffer.write(chunk, 0, bytesRead);
                }
                this.bodyBytes = buffer.toByteArray();
            }
        } else {
            this.bodyBytes = new byte[0];
        }
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(statusCode);
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return statusCode;
    }

    @Override
    public String getStatusText() throws IOException {
        return reasonPhrase;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(bodyBytes);
    }

    @Override
    public void close() {
        // Nothing to close - response was already consumed and buffered
    }
}
