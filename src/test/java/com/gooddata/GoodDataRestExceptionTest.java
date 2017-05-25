/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import com.gooddata.gdc.GdcError;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GoodDataRestExceptionTest {
    @Test
    public void shouldCreateDefaultInstance() throws Exception {
        final GoodDataRestException e = new GoodDataRestException(500, "a123", "message", "component", "gdc.error", "code");
        assertThat(e.getMessage(), is("500: [requestId=a123] message"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getRequestId(), is("a123"));
        assertThat(e.getText(), is("message"));
        assertThat(e.getComponent(), is("component"));
        assertThat(e.getErrorClass(), is("gdc.error"));
        assertThat(e.getErrorCode(), is("code"));
    }

    @Test
    public void shouldCreateDefaultInstanceWithoutErrorCode() throws Exception {
        final GoodDataRestException e = new GoodDataRestException(500, "a123", "message", "component", "gdc.error");
        assertThat(e.getMessage(), is("500: [requestId=a123] message"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getRequestId(), is("a123"));
        assertThat(e.getText(), is("message"));
        assertThat(e.getComponent(), is("component"));
        assertThat(e.getErrorClass(), is("gdc.error"));
    }

    @Test
    public void shouldCreateInstanceWithoutRequestId() throws Exception {
        final GoodDataRestException e = new GoodDataRestException(500, null, "message", "component", "gdc.error");
        assertThat(e.getMessage(), is("500: message"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getText(), is("message"));
        assertThat(e.getComponent(), is("component"));
        assertThat(e.getErrorClass(), is("gdc.error"));
    }

    @Test
    public void shouldCreateInstanceWithNullGdcError() throws Exception {
        final GoodDataRestException e = new GoodDataRestException(500, "a123", "message", null);
        assertThat(e.getMessage(), is("500: [requestId=a123] message"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getRequestId(), is("a123"));
        assertThat(e.getText(), is("message"));
    }

    @Test
    public void shouldCreateInstanceWithNullStatusAndGdcError() throws Exception {
        final GoodDataRestException e = new GoodDataRestException(500, "a123", null, null);
        assertThat(e.getMessage(), is("500: [requestId=a123] Unknown error"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getRequestId(), is("a123"));
        assertThat(e.getText(), is(nullValue()));
    }

    @Test
    public void shouldCreateInstanceWithGdcError() throws Exception {
        final GdcError err = readObjectFromResource("/gdc/gdcError.json", GdcError.class);

        final GoodDataRestException e = new GoodDataRestException(500, "a123", "message", err);
        assertThat(e.getMessage(), is("500: [requestId=REQ] MSG"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getText(), is("MSG"));
        assertThat(e.getErrorClass(), is("CLASS"));
        assertThat(e.getComponent(), is("COMPONENT"));
        assertThat(e.getRequestId(), is("REQ"));
        assertThat(e.getErrorCode(), is("CODE"));
    }
}
