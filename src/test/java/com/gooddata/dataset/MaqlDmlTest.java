/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaqlDmlTest {

    @Test
    public void testSerialization() throws Exception {
        final String json = IOUtils.toString(getClass().getResourceAsStream("/dataset/maqlDml.json"), "UTF-8");
        assertThat(new ObjectMapper().writeValueAsString(new MaqlDml("maqlddl")),
                is(json));
    }
}
