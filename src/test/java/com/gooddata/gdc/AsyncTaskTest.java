/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AsyncTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/gdc/asyncTask.json");
        final AsyncTask asyncTask = new ObjectMapper().readValue(is, AsyncTask.class);

        assertThat(asyncTask.getUri(), is("POLL-URI"));
    }
}
