/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
