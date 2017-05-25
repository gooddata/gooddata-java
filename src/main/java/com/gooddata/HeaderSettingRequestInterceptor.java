/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.util.Map;

import static com.gooddata.util.Validate.notNull;

/**
 * Intercepts client-side HTTP requests and sets HTTP headers passed to constructor of this class.
 * Implementations of this interface can be registered with the RestTemplate, as to modify the outgoing
 * ClientHttpRequest and/or the incoming ClientHttpResponse.
 */
class HeaderSettingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Map<String, String> headers;

    /**
     * Construct interceptor for setting given HTTP headers.
     *
     * @param headers the map of HTTP header names to header values
     */
    public HeaderSettingRequestInterceptor(Map<String, String> headers) {
        this.headers = notNull(headers, "headers");
    }

    /**
     * Intercept the given request, set headers passed to constructor, and return a response.
     * The given {@link org.springframework.http.client.ClientHttpRequestExecution} allows the interceptor
     * to pass on the request and response to the next entity in the chain.
     *
     * @param request   the request, containing method, URI, and headers
     * @param body      the body of the request
     * @param execution the request execution
     * @return the response
     * @throws IOException in case of I/O errors
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        final HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        for (final Map.Entry<String, String> header : headers.entrySet()) {
            requestWrapper.getHeaders().set(header.getKey(), header.getValue());
        }
        return execution.execute(requestWrapper, body);
    }
}
