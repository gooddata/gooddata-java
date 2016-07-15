/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class LinkEntriesTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/gdc/linkEntries.json");
        final LinkEntries linkEntries = new ObjectMapper().readValue(stream, LinkEntries.class);

        assertThat(linkEntries, is(notNullValue()));
        assertThat(linkEntries.getEntries(), hasSize(1));
        assertThat(linkEntries.getEntries().get(0), is(notNullValue()));
        assertThat(linkEntries.getEntries().get(0).getLink(), is("URI"));
        assertThat(linkEntries.getEntries().get(0).getUri(), is("URI"));
        assertThat(linkEntries.getEntries().get(0).getCategory(), is("tasks-status"));
    }
}
