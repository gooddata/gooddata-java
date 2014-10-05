/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MaqlDdlTest {

    @Test
    public void testSerialization() throws Exception {
        assertThat(new ObjectMapper().writeValueAsString(new MaqlDdl("maqlddl")),
                is("{\"manage\":{\"maql\":\"maqlddl\"}}"));

    }
}
