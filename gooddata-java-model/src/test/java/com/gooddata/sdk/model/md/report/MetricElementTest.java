/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.gooddata.sdk.model.md.Metric;
import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricElementTest {

    private static final String URI = "/ELEM_URI";
    private static final String ALIAS = "ELEM_ALIAS";
    private static final String FORMAT = "FORMAT";
    private static final String DRILL_URI = "/DRILL_URI";

    @Test
    public void testDeserialization() throws Exception {
        final MetricElement element = readObjectFromResource("/md/report/metricElement.json", MetricElement.class);
        assertThat(element, is(notNullValue()));
        assertThat(element.getUri(), is(URI));
        assertThat(element.getAlias(), is(ALIAS));
        assertThat(element.getFormat(), is(FORMAT));
        assertThat(element.getDrillAcrossStepAttributeDisplayFormUri(), is(DRILL_URI));
    }

    @Test
    public void testSerialization() throws Exception {
        final MetricElement element = new MetricElement(URI, ALIAS, FORMAT, DRILL_URI);
        assertThat(element, jsonEquals(resource("md/report/metricElement.json")));
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

    @Test
    public void testToStringFormat() {
        final MetricElement element = new MetricElement(URI, ALIAS, FORMAT, DRILL_URI);

        assertThat(element.toString(), matchesPattern(MetricElement.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final MetricElement element = new MetricElement(URI, ALIAS, FORMAT, DRILL_URI);
        final MetricElement deserialized = SerializationUtils.roundtrip(element);

        assertThat(deserialized, jsonEquals(element));
    }
}
