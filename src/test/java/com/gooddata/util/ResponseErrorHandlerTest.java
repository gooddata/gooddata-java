/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.GoodData;
import com.gooddata.GoodDataRestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseErrorHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ResponseErrorHandler responseErrorHandler;

    @BeforeMethod
    public void setUp() throws Exception {
        responseErrorHandler = new ResponseErrorHandler(singletonList(new MappingJackson2HttpMessageConverter(OBJECT_MAPPER)));
    }

    @Test
    public void testHandleGdcError() throws Exception {
        final ClientHttpResponse response = prepareResponse("/gdc/gdcError.json");

        final GoodDataRestException exc = assertException(response);

        assertThat("GoodDataRestException should have been thrown!", exc, is(notNullValue()));
        assertThat(exc.getStatusCode(), is(500));
        assertThat(exc.getRequestId(), is("REQ"));
        assertThat(exc.getComponent(), is("COMPONENT"));
        assertThat(exc.getErrorClass(), is("CLASS"));
        assertThat(exc.getErrorCode(), is("CODE"));
        assertThat(exc.getText(), is("MSG"));
    }

    @Test
    public void testHandleErrorStructure() throws Exception {
        final ClientHttpResponse response = prepareResponse("/gdc/errorStructure.json");

        final GoodDataRestException exc = assertException(response);

        assertThat("GoodDataRestException should have been thrown!", exc, is(notNullValue()));
        assertThat(exc.getStatusCode(), is(500));
        assertThat(exc.getRequestId(), is("REQ"));
        assertThat(exc.getComponent(), is("COMPONENT"));
        assertThat(exc.getErrorClass(), is("CLASS"));
        assertThat(exc.getErrorCode(), is("CODE"));
        assertThat(exc.getText(), is("MSG PARAM1 PARAM2 3"));
    }

    @Test
    public void testHandleInvalidError() throws Exception {
        final ClientHttpResponse response = prepareResponse("/gdc/invalidError.json");

        final GoodDataRestException exc = assertException(response);

        assertThat("GoodDataRestException should have been thrown!", exc, is(notNullValue()));
        assertThat(exc.getStatusCode(), is(500));
        assertThat(exc.getRequestId(), is("requestId"));
        assertThat(exc.getComponent(), is(nullValue()));
        assertThat(exc.getErrorClass(), is(nullValue()));
        assertThat(exc.getErrorCode(), is(nullValue()));
        assertThat(exc.getText(), is(nullValue()));
    }

    private ClientHttpResponse prepareResponse(String resourcePath) throws IOException {
        final ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(response.getRawStatusCode()).thenReturn(500);
        final HttpHeaders headers = new HttpHeaders();
        headers.set(GoodData.GDC_REQUEST_ID_HEADER, "requestId");
        headers.setContentType(MediaType.APPLICATION_JSON);
        when(response.getHeaders()).thenReturn(headers);
        when(response.getBody()).thenReturn(getClass().getResourceAsStream(resourcePath));

        return response;
    }

    private GoodDataRestException assertException(ClientHttpResponse response) {
        try {
            responseErrorHandler.handleError(response);
            throw new AssertionError("Expected GoodDataRestException");
        } catch (GoodDataRestException e) {
            return e;
        }
    }

}