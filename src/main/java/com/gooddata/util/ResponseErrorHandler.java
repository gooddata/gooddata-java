/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.ErrorStructure;
import com.gooddata.gdc.Header;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;

import static com.gooddata.util.Validate.noNullElements;

/**
 * A response error handler able to extract GoodData error response
 */
public class ResponseErrorHandler extends DefaultResponseErrorHandler {

    private final HttpMessageConverterExtractor<ErrorStructure> gdcErrorExtractor;

    public ResponseErrorHandler(List<HttpMessageConverter<?>> messageConverters) {
        gdcErrorExtractor = new HttpMessageConverterExtractor<>(ErrorStructure.class,
                noNullElements(messageConverters, "messageConverters"));
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        ErrorStructure error = null;
        try {
            error = gdcErrorExtractor.extractData(response);
        } catch (RestClientException | IOException | HttpMessageConversionException ignored) {
        }
        final String requestId = response.getHeaders().getFirst(Header.GDC_REQUEST_ID);
        int statusCode;
        try {
            statusCode = response.getRawStatusCode();
        } catch (IOException e) {
            statusCode = 0;
        }
        String statusText;
        try {
            statusText = response.getStatusText();
        } catch (IOException e) {
            statusText = null;
        }
        throw new GoodDataRestException(statusCode, requestId, statusText, error);
    }

}
