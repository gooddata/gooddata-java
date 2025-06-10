/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.EntityDetails;

import java.io.IOException;

import static com.gooddata.sdk.common.gdc.Header.GDC_REQUEST_ID;

/**
 * Intercepts responses to check if they have set the X-GDC-REQUEST header for easier debugging.
 * If not, it takes this header from the request sent.
 */
public class ResponseMissingRequestIdInterceptor implements HttpResponseInterceptor {
    @Override
    public void process(final HttpResponse response, final EntityDetails entity, final HttpContext context) throws HttpException, IOException {
        if (response.getFirstHeader(GDC_REQUEST_ID) == null) {
            HttpCoreContext coreContext = HttpCoreContext.cast(context);
            HttpRequest request = coreContext.getRequest(); // Modern, non-deprecated
            if (request != null) {
                Header requestIdHeader = request.getFirstHeader(GDC_REQUEST_ID);
                if (requestIdHeader != null) {
                    response.setHeader(GDC_REQUEST_ID, requestIdHeader.getValue());
                }
            }
        }
    }
}
