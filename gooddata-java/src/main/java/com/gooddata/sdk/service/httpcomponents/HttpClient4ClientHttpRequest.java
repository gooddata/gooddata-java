/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * {@link org.springframework.http.client.ClientHttpRequest} implementation that uses
 * Apache HttpClient 4.x for execution. Compatible with Spring 6.
 */
final class HttpClient4ClientHttpRequest extends AbstractClientHttpRequest {

    private final HttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

    HttpClient4ClientHttpRequest(HttpClient httpClient, HttpUriRequest httpRequest, HttpContext httpContext) {
        this.httpClient = httpClient;
        this.httpRequest = httpRequest;
        this.httpContext = httpContext;
    }

    @Override
    public String getMethodValue() {
        return this.httpRequest.getMethod();
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.httpRequest.getMethod());
    }

    @Override
    public URI getURI() {
        return this.httpRequest.getURI();
    }

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) {
        return this.bufferedOutput;
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        byte[] bytes = this.bufferedOutput.toByteArray();
        if (bytes.length > 0) {
            if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) this.httpRequest;
                HttpEntity requestEntity = new ByteArrayEntity(bytes);
                entityRequest.setEntity(requestEntity);
            }
        }

        HttpHeaders headersToUse = getHeaders();
        headersToUse.putAll(headers);

        // Set headers on the request (skip Content-Length as HttpClient sets it automatically)
        for (String headerName : headersToUse.keySet()) {
            if (!"Content-Length".equalsIgnoreCase(headerName)) {
                String headerValue = StringUtils.collectionToCommaDelimitedString(headersToUse.get(headerName));
                this.httpRequest.setHeader(headerName, headerValue);
            }
        }

        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpClient4ClientHttpResponse(httpResponse);
    }
}

