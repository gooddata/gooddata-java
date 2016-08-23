/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.md.Metric;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricElementTest {

    private static final String URI = "/ELEM_URI";
    private static final String ALIAS = "ELEM_ALIAS";
    private static final String FORMAT = "FORMAT";
    private static final String DRILL_URI = "/DRILL_URI";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/metricElement.json");
        final MetricElement element = new ObjectMapper().readValue(is, MetricElement.class);
        assertThat(element, is(notNullValue()));
        assertThat(element.getUri(), is(URI));
        assertThat(element.getAlias(), is(ALIAS));
        assertThat(element.getFormat(), is(FORMAT));
        assertThat(element.getDrillAcrossStepAttributeDisplayFormUri(), is(DRILL_URI));
    }

    @Test
    public void testSerialization() throws Exception {
        final MetricElement element = new MetricElement(URI, ALIAS, FORMAT, DRILL_URI);
        assertThat(element, serializesToJson("/md/report/metricElement.json"));
    }

    @Test
    public void testCreateFromMetric() throws Exception {
        final Metric metric = mock(Metric.class);
        when(metric.getTitle()).thenReturn(ALIAS);
        when(metric.getUri()).thenReturn(URI);

        final MetricElement element = new MetricElement(metric);
        assertThat(element.getUri(), is(URI));
        assertThat(element.getAlias(), is(ALIAS));
    }
}