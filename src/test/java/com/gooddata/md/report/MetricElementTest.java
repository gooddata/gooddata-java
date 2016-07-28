/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetricElementTest {

    public static final String URI = "/ELEM_URI";
    public static final String ALIAS = "ELEM_ALIAS";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/metricElement.json");
        final MetricElement element = new ObjectMapper().readValue(is, MetricElement.class);
        assertThat(element, is(notNullValue()));
        assertThat(element.getUri(), is(URI));
        assertThat(element.getAlias(), is(ALIAS));
    }

    @Test
    public void testSerialization() throws Exception {
        final MetricElement element = new MetricElement(URI, ALIAS);
        assertThat(element, serializesToJson("/md/report/metricElement.json"));
    }

}