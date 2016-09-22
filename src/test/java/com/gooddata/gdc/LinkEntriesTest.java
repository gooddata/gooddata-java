/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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

    @SuppressWarnings("deprecation")
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
