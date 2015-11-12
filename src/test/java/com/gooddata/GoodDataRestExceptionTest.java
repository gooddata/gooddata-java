package com.gooddata;

import com.gooddata.gdc.GdcError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class GoodDataRestExceptionTest {
    @Test
    public void shouldCreateDefaultInstance() throws Exception {
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
    public void shouldCreateInstanceWithGdcError() throws Exception {
        final InputStream inputStream = getClass().getResourceAsStream("/gdc/gdcError.json");
        final GdcError err = new ObjectMapper().readValue(inputStream, GdcError.class);

        final GoodDataRestException e = new GoodDataRestException(500, "a123", "message", err);
        assertThat(e.getMessage(), is("500: [requestId=REQ] MSG"));
        assertThat(e.getStatusCode(), is(500));
        assertThat(e.getText(), is("MSG"));
        assertThat(e.getErrorClass(), is("CLASS"));
        assertThat(e.getComponent(), is("COMPONENT"));
        assertThat(e.getRequestId(), is("REQ"));

    }
}