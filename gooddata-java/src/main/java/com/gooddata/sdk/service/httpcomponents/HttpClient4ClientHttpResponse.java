/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ClientHttpResponse} implementation that uses
 * Apache HttpClient 4.x. Compatible with Spring 6.
 */
final class HttpClient4ClientHttpResponse implements ClientHttpResponse {

    private final HttpResponse httpResponse;

    private HttpHeaders headers;

    HttpClient4ClientHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(this.httpResponse.getStatusLine().getStatusCode());
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return this.httpResponse.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return this.httpResponse.getStatusLine().getReasonPhrase();
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            for (Header header : this.httpResponse.getAllHeaders()) {
                this.headers.add(header.getName(), header.getValue());
            }
        }
        return this.headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        HttpEntity entity = this.httpResponse.getEntity();
        return (entity != null ? entity.getContent() : StreamUtils.emptyInput());
    }

    @Override
    public void close() {
        try {
            try {
                // Consume the response body to ensure proper connection reuse
                StreamUtils.drain(getBody());
            }
            finally {
                // Only close if it's a CloseableHttpResponse
                if (this.httpResponse instanceof CloseableHttpResponse) {
                    ((CloseableHttpResponse) this.httpResponse).close();
                }
            }
        }
        catch (IOException ex) {
            // Ignore exception on close
        }
    }
}

