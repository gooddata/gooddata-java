/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.github.sardine.impl.SardineException;
import com.github.sardine.impl.handler.ValidatingResponseHandler;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;

import static com.gooddata.sdk.common.gdc.Header.GDC_REQUEST_ID;

/**
 * A basic validation response handler that extends the functionality of {@link ValidatingResponseHandler}
 * about the addition of X-GDC-REQUEST header to exception.
 */
class GdcSardineResponseHandler implements ResponseHandler<Void> {

    @Override
    public Void handleResponse(final HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final int statusCode = statusLine.getStatusCode();
        if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES)
        {
            return null;
        }
        final Header requestIdHeader = response.getFirstHeader(GDC_REQUEST_ID);
        if (requestIdHeader != null) {
            throw new GdcSardineException(requestIdHeader.getValue(), "Unexpected response", statusLine.getStatusCode(),
                    statusLine.getReasonPhrase()
            );
        } else {
            throw new SardineException("Unexpected response", statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
    }
}
