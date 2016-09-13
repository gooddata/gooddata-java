/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueryTest {

    @Test
    public void testDeserialization() throws Exception {
        final Query query = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/query.json"), Query.class);
        assertThat(query, is(notNullValue()));
        assertThat(query.getCategory(), is("MD::Query::Object"));
        assertThat(query.getTitle(), is("List of allTypes"));
        assertThat(query.getSummary(), is("Metadata Query Resources for project 'PROJ_ID'"));
    }
}