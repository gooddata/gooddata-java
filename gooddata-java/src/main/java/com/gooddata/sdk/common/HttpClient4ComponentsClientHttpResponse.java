/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Spring 6 compatible {@link ClientHttpResponse} implementation that wraps Apache HttpComponents HttpClient 4.x response.
 * This bridges HttpClient 4.x responses with Spring 6's ClientHttpResponse interface.
 * Package-private as it's only used internally within the common package.
 */
class HttpClient4ComponentsClientHttpResponse implements ClientHttpResponse {

    private final org.apache.http.HttpResponse httpResponse;
    private HttpHeaders headers;

    public HttpClient4ComponentsClientHttpResponse(org.apache.http.HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(httpResponse.getStatusLine().getStatusCode());
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return httpResponse.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return httpResponse.getStatusLine().getReasonPhrase();
    }

    @Override
    public HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpHeaders();
            for (Header header : httpResponse.getAllHeaders()) {
                headers.add(header.getName(), header.getValue());
            }
        }
        return headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        return (entity != null) ? entity.getContent() : new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public void close() {
        // HttpClient 4.x doesn't require explicit connection closing in most cases
        // The connection is managed by the connection manager
        try {
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null && entity.getContent() != null) {
                entity.getContent().close();
            }
        } catch (IOException e) {
            // Ignore close exceptions
        }
    }
}
