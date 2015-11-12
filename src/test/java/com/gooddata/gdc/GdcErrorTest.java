/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GdcErrorTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream inputStream = getClass().getResourceAsStream("/gdc/gdcError.json");
        final GdcError err = new ObjectMapper().readValue(inputStream, GdcError.class);

        assertThat(err, is(notNullValue()));
        assertThat(err.getErrorClass(), is("CLASS"));
        assertThat(err.getTrace(), is("TRACE"));
        assertThat(err.getMessage(), is("MSG"));
        assertThat(err.getComponent(), is("COMPONENT"));
        assertThat(err.getErrorId(), is("ID"));
        assertThat(err.getErrorCode(), is("CODE"));
        assertThat(err.getRequestId(), is("REQ"));

        assertThat(err.getParameters(), is(notNullValue()));
        assertThat(err.getParameters().length, is(2));
        assertThat(err.getParameters()[0].toString(), is("PARAM1"));
        assertThat(err.getParameters()[1].toString(), is("PARAM2"));
    }
}