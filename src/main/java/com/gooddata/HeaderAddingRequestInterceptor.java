/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang.Validate.notNull;

/**
 */
public class HeaderAddingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Map<String, String> headers;

    public HeaderAddingRequestInterceptor(Map<String, String> headers) {
        notNull(headers);
        this.headers = headers;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        for (final Map.Entry<String,String> header: headers.entrySet()) {
            requestWrapper.getHeaders().set(header.getKey(), header.getValue());
        }
        return execution.execute(requestWrapper, body);
    }
}
