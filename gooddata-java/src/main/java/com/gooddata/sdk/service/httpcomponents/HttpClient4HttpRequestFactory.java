/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;

/**
 * Custom HttpClient 4.x compatible request factory for Spring 6.
 * This is needed because Spring 6's default HttpComponentsClientHttpRequestFactory
 * expects HttpClient 5.x while we want to maintain compatibility with HttpClient 4.5.x.
 * Package-private as it's currently unused and should not be part of the public API.
 */
class HttpClient4HttpRequestFactory implements ClientHttpRequestFactory {

    private final HttpClient httpClient;
    private HttpContext httpContext;

    public HttpClient4HttpRequestFactory(HttpClient httpClient) {
        Assert.notNull(httpClient, "HttpClient must not be null");
        this.httpClient = httpClient;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
        postProcessHttpRequest(httpRequest);
        
        HttpContext context = createHttpContext(httpMethod, uri);
        if (context == null) {
            context = HttpClientContext.create();
        }
        
        return new HttpClient4ClientHttpRequest(httpClient, httpRequest, context);
    }

    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        if (httpMethod == HttpMethod.GET) {
            return new org.apache.http.client.methods.HttpGet(uri);
        } else if (httpMethod == HttpMethod.HEAD) {
            return new org.apache.http.client.methods.HttpHead(uri);
        } else if (httpMethod == HttpMethod.POST) {
            return new org.apache.http.client.methods.HttpPost(uri);
        } else if (httpMethod == HttpMethod.PUT) {
            return new org.apache.http.client.methods.HttpPut(uri);
        } else if (httpMethod == HttpMethod.PATCH) {
            return new org.apache.http.client.methods.HttpPatch(uri);
        } else if (httpMethod == HttpMethod.DELETE) {
            return new org.apache.http.client.methods.HttpDelete(uri);
        } else if (httpMethod == HttpMethod.OPTIONS) {
            return new org.apache.http.client.methods.HttpOptions(uri);
        } else if (httpMethod == HttpMethod.TRACE) {
            return new org.apache.http.client.methods.HttpTrace(uri);
        } else {
            throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
        }
    }

    protected void postProcessHttpRequest(HttpUriRequest request) {
        // Template method for subclasses to override
    }

    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return this.httpContext;
    }
}

