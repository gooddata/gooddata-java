/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

import static com.gooddata.gdc.Header.GDC_REQUEST_ID;

/**
 * Intercepts the client-side requests on low-level in order to be able to catch requests also from the Sardine,
 * that is working independently from Spring {@link org.springframework.web.client.RestTemplate} to set
 * the X-GDC-REQUEST header to them.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestIdInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        final StringBuilder requestIdBuilder = new StringBuilder();
        final Header requestIdHeader = request.getFirstHeader(GDC_REQUEST_ID);
        if (requestIdHeader != null) {
            requestIdBuilder.append(requestIdHeader.getValue()).append(":");
        }
        final String requestId = requestIdBuilder.append(RandomStringUtils.randomAlphanumeric(16)).toString();
        request.setHeader(GDC_REQUEST_ID, requestId);
    }
}
