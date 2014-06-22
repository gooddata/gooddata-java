/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffRequestTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        String valueAsString = mapper.writeValueAsString(new DiffRequest("{\"projectModel\":\"xxx\"}"));
        assertEquals("{\"diffRequest\":{\"targetModel\":{\"projectModel\":\"xxx\"}}}", valueAsString);
    }
}
