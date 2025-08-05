/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.EntityDetails;


import java.io.IOException;

import static com.gooddata.sdk.common.gdc.Header.GDC_REQUEST_ID;

public class RequestIdInterceptor implements HttpRequestInterceptor {

    @Override
    public void process(final HttpRequest request, final EntityDetails entity, final HttpContext context)
            throws HttpException, IOException {
        final StringBuilder requestIdBuilder = new StringBuilder();
        final String requestIdHeader = request.getFirstHeader(GDC_REQUEST_ID) != null ?
            request.getFirstHeader(GDC_REQUEST_ID).getValue() : null;
        if (requestIdHeader != null) {
            requestIdBuilder.append(requestIdHeader).append(":");
        }
        final String requestId = requestIdBuilder.append(RandomStringUtils.randomAlphanumeric(16)).toString();
        request.setHeader(GDC_REQUEST_ID, requestId);
    }
}
