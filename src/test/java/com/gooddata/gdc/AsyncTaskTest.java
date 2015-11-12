/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AsyncTaskTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/gdc/asyncTask.json");
        final AsyncTask asyncTask = MAPPER.readValue(is, AsyncTask.class);

        assertThat(asyncTask.getUri(), is("POLL-URI"));
    }

    @Test
    public void testSerialization() throws Exception {
        final String json = MAPPER.writeValueAsString(new AsyncTask("foo"));
        final AsyncTask asyncTask = MAPPER.readValue(json, AsyncTask.class);

        assertThat(asyncTask.getUri(), is("foo"));
    }
}
