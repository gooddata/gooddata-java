/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

import java.io.IOException;

import static com.gooddata.sdk.common.gdc.Header.GDC_REQUEST_ID;

/**
 * Intercepts the client-side requests on low-level in order to be able to catch requests also from the Sardine,
 * that is working independently from Spring {@link org.springframework.web.client.RestTemplate} to set
 * the X-GDC-REQUEST header to them.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestIdInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final org.apache.hc.core5.http.EntityDetails entity, final HttpContext context) throws HttpException, IOException {
        final StringBuilder requestIdBuilder = new StringBuilder();
        final Header requestIdHeader = request.getFirstHeader(GDC_REQUEST_ID);
        if (requestIdHeader != null) {
            requestIdBuilder.append(requestIdHeader.getValue()).append(":");
        }
        final String requestId = requestIdBuilder.append(RandomStringUtils.secure().nextAlphanumeric(16)).toString();
        request.setHeader(GDC_REQUEST_ID, requestId);
    }
}

