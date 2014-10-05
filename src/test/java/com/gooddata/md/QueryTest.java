/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

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