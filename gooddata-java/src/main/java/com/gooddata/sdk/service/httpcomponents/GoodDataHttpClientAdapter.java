/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.http.client.GoodDataHttpClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Adapter that wraps GoodDataHttpClient to implement the HttpClient interface.
 * This is needed because GoodDataHttpClient from gooddata-http-client library
 * has the same methods as HttpClient but doesn't formally implement the interface.
 * 
 * <p>This adapter delegates all execute() calls to the wrapped GoodDataHttpClient,
 * converting {@link HttpException} to {@link HttpProtocolException} where necessary
 * for interface compliance while preserving exception details for debugging.
 * 
 * @since 4.0.4
 */
class GoodDataHttpClientAdapter implements HttpClient {

    private final GoodDataHttpClient goodDataHttpClient;

    public GoodDataHttpClientAdapter(GoodDataHttpClient goodDataHttpClient) {
        this.goodDataHttpClient = goodDataHttpClient;
    }

    @Override
    public ClassicHttpResponse execute(HttpHost target, ClassicHttpRequest request, HttpContext context) throws IOException {
        return goodDataHttpClient.execute(target, request, context);
    }

    @Override
    public ClassicHttpResponse execute(HttpHost target, ClassicHttpRequest request) throws IOException {
        return goodDataHttpClient.execute(target, request);
    }

    @Override
    public <T> T execute(HttpHost target, ClassicHttpRequest request, HttpContext context,
                         HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        try {
            return goodDataHttpClient.execute(target, request, context, responseHandler);
        } catch (HttpException e) {
            // Preserve exception context for debugging
            final String targetInfo = target != null ? target.toURI() : "no-target-specified";
            final String requestInfo = request != null ? request.getMethod() + " " + request.getRequestUri() : "no-request";
            throw new HttpProtocolException(
                "HTTP protocol error during request execution: " + e.getMessage() + 
                " [target=" + targetInfo + ", request=" + requestInfo + "]", 
                e);
        }
    }

    @Override
    public <T> T execute(HttpHost target, ClassicHttpRequest request,
                         HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        return execute(target, request, null, responseHandler);
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, HttpContext context) throws IOException {
        // Validate request is not null to prevent NPE
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        // HttpClient 5.x allows null target - the target is determined from the request URI
        // This is standard behavior when using absolute URIs in requests
        return execute(null, request, context);
    }

    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request) throws IOException {
        // Validate request is not null to prevent NPE
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        // HttpClient 5.x allows null target - the target is determined from the request URI
        return execute(null, request);
    }

    @Override
    public <T> T execute(ClassicHttpRequest request, HttpContext context,
                         HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        // Validate inputs to prevent NPE
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (responseHandler == null) {
            throw new IllegalArgumentException("Response handler cannot be null");
        }
        // HttpClient 5.x allows null target - the target is determined from the request URI
        return execute(null, request, context, responseHandler);
    }

    @Override
    public <T> T execute(ClassicHttpRequest request,
                         HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
        // Validate inputs to prevent NPE
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (responseHandler == null) {
            throw new IllegalArgumentException("Response handler cannot be null");
        }
        return execute(null, request, responseHandler);
    }
}

