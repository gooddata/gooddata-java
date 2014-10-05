/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GridElementTest {

    public static final String URI = "/ELEM_URI";
    public static final String ALIAS = "ELEM_ALIAS";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/gridElement.json");
        final GridElement element = new ObjectMapper().readValue(is, GridElement.class);
        assertThat(element, is(notNullValue()));
        assertThat(element.getUri(), is(URI));
        assertThat(element.getAlias(), is(ALIAS));
    }

    @Test
    public void testSerialization() throws Exception {
        final GridElement element = new GridElement(URI, ALIAS);
        assertThat(element, serializesToJson("/md/report/gridElement.json"));
    }

}