/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class AsyncTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/gdc/asyncTask.json");
        final AsyncTask asyncTask = new ObjectMapper().readValue(is, AsyncTask.class);

        assertEquals("POLL-URI", asyncTask.getUri());
    }
}
