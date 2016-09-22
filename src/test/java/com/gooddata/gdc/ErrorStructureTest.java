/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ErrorStructureTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream inputStream = getClass().getResourceAsStream("/gdc/errorStructure.json");
        final ErrorStructure errStructure = new ObjectMapper().readValue(inputStream, ErrorStructure.class);

        assertThat(errStructure, is(notNullValue()));
        assertThat(errStructure.getErrorClass(), is("CLASS"));
        assertThat(errStructure.getTrace(), is("TRACE"));
        assertThat(errStructure.getMessage(), is("MSG %s %s %d"));
        assertThat(errStructure.getComponent(), is("COMPONENT"));
        assertThat(errStructure.getErrorId(), is("ID"));
        assertThat(errStructure.getErrorCode(), is("CODE"));
        assertThat(errStructure.getRequestId(), is("REQ"));

        assertThat(errStructure.getParameters(), is(notNullValue()));
        assertThat(errStructure.getParameters().length, is(3));
        assertThat(errStructure.getParameters()[0].toString(), is("PARAM1"));
        assertThat(errStructure.getParameters()[1].toString(), is("PARAM2"));
        assertThat(errStructure.getParameters()[2].toString(), is("3"));

        assertThat(errStructure.getFormattedMessage(), is("MSG PARAM1 PARAM2 3"));
    }
}