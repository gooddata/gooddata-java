/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.gdc.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

/**
 * Intercepts the request-response to check for X-GDC-DEPRECATED header and log it's content.
 */
class DeprecationWarningRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger("com.gooddata.sdk.DeprecationWarning");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        final ClientHttpResponse response = execution.execute(request, body);
        if (logger.isWarnEnabled()) {
            final List<String> deprecated = response.getHeaders().get(Header.GDC_DEPRECATED);
            if (deprecated != null && !deprecated.isEmpty()) {
                final List<String> versionHeaders = request.getHeaders().get(Header.GDC_VERSION);
                final String version = versionHeaders != null && !versionHeaders.isEmpty() ? versionHeaders.get(0) : "UNKNOWN";
                logger.warn("Resource path={} version={} deprecated {}", request.getURI().getPath(), version, deprecated.get(0));
            }
        }
        return response;
    }
}
