/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DiffRequestTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        String valueAsString = mapper.writeValueAsString(new DiffRequest("{\"projectModel\":\"xxx\"}"));
        assertThat(valueAsString, is("{\"diffRequest\":{\"targetModel\":{\"projectModel\":\"xxx\"}}}"));
    }
}
