package com.gooddata.gdc;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

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
        assertThat(errStructure.getMessage(), is("MSG"));
        assertThat(errStructure.getComponent(), is("COMPONENT"));
        assertThat(errStructure.getErrorId(), is("ID"));
        assertThat(errStructure.getErrorCode(), is("CODE"));
        assertThat(errStructure.getRequestId(), is("REQ"));

        assertThat(errStructure.getParameters(), is(notNullValue()));
        assertThat(errStructure.getParameters().length, is(2));
        assertThat(errStructure.getParameters()[0], is("PARAM1"));
        assertThat(errStructure.getParameters()[1], is("PARAM2"));
    }
}