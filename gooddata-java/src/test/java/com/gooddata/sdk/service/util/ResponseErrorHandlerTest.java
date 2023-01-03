/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.util;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.gdc.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseErrorHandlerTest {

    private ResponseErrorHandler responseErrorHandler;

    @BeforeMethod
    public void setUp() throws Exception {
        responseErrorHandler = new ResponseErrorHandler(singletonList(new MappingJackson2HttpMessageConverter(OBJECT_MAPPER)));
    }

    @Test
    public void testHandleGdcError() throws Exception {
        final ClientHttpResponse response = prepareResponse("/gdc/gdcError.json");

        final GoodDataRestException exc = assertException(response);

        assertThat(exc.getMessage(), is("500: [request_id=REQ] MSG: PARAM1, PARAM2"));
        assertThat(exc.getStatusCode(), is(500));
        assertThat(exc.getRequestId(), is("REQ"));
        assertThat(exc.getComponent(), is("COMPONENT"));
        assertThat(exc.getErrorClass(), is("CLASS"));
        assertThat(exc.getErrorCode(), is("CODE"));
        assertThat(exc.getText(), is("MSG: PARAM1, PARAM2"));
    }

    @Test
    public void testHandleErrorStructure() throws Exception {
        final ClientHttpResponse response = prepareResponse("/gdc/errorStructure.json");

        final GoodDataRestException exc = assertException(response);

        assertThat(exc.getMessage(), is("500: [request_id=REQ] MSG PARAM1 PARAM2 3"));
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

        assertThat(exc.getMessage(), is("500: [request_id=requestId] Unknown error"));
        assertThat(exc.getStatusCode(), is(500));
        assertThat(exc.getRequestId(), is("requestId"));
        assertThat(exc.getComponent(), is(nullValue()));
        assertThat(exc.getErrorClass(), is(nullValue()));
        assertThat(exc.getErrorCode(), is(nullValue()));
        assertThat(exc.getText(), is(nullValue()));
    }

    @Test
    public void shouldName() throws Exception {
        final ClientHttpResponse response = prepareResponse();
        final HttpHeaders headers = new HttpHeaders();
        when(response.getHeaders()).thenReturn(headers);
        when(response.getStatusText()).thenThrow(IOException.class);
        when(response.getRawStatusCode()).thenThrow(IOException.class);

        final GoodDataRestException exc = assertException(response);

        assertThat(exc.getMessage(), is("0: Unknown error"));
        assertThat(exc.getStatusCode(), is(0));
        assertThat(exc.getRequestId(), is(nullValue()));
        assertThat(exc.getComponent(), is(nullValue()));
        assertThat(exc.getErrorClass(), is(nullValue()));
        assertThat(exc.getErrorCode(), is(nullValue()));
        assertThat(exc.getText(), is(nullValue()));
    }

    private ClientHttpResponse prepareResponse(String resourcePath) throws IOException {
        final ClientHttpResponse response = prepareResponse();
        final HttpHeaders headers = new HttpHeaders();
        when(response.getHeaders()).thenReturn(headers);
        headers.set(Header.GDC_REQUEST_ID, "requestId");
        headers.setContentType(MediaType.APPLICATION_JSON);
        when(response.getBody()).thenReturn(readFromResource(resourcePath));
        return response;
    }

    private ClientHttpResponse prepareResponse() throws IOException {
        final ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(response.getRawStatusCode()).thenReturn(500);
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
