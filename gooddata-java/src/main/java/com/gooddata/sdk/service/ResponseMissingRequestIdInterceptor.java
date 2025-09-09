/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import java.io.IOException;

import static com.gooddata.sdk.common.gdc.Header.GDC_REQUEST_ID;

/**
 * Intercepts responses to check if they have set the X-GDC-REQUEST header for easier debugging.
 * If not, it takes this header from the request sent.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class ResponseMissingRequestIdInterceptor implements HttpResponseInterceptor {

    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {

        if (response.getFirstHeader(GDC_REQUEST_ID) == null) {
            final HttpCoreContext coreContext = HttpCoreContext.adapt(context);
            final Header requestIdHeader = coreContext.getRequest().getFirstHeader(GDC_REQUEST_ID);
            response.setHeader(GDC_REQUEST_ID, requestIdHeader.getValue());
        }
    }
}

