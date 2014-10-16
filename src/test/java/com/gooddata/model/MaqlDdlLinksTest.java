/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaqlDdlLinksTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/model/maqlDdlLinks.json");
        final MaqlDdlLinks maqlDdlLinks = new ObjectMapper().readValue(stream, MaqlDdlLinks.class);

        assertThat(maqlDdlLinks, is(notNullValue()));
        assertThat(maqlDdlLinks.getStatusLink(), is("/gdc/md/PROJECT_ID/tasks/123/status"));
    }
}