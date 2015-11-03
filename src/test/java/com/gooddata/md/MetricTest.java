/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MetricTest {

    @Test
    public void testDeserialization() throws Exception {
        final Metric metric = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/md/metric-out.json"), Metric.class);
        assertThat(metric, is(notNullValue()));
        assertThat(metric.getExpression(), is("SELECT AVG([/gdc/md/PROJECT_ID/obj/EXPR_ID])"));
        assertThat(metric.getFormat(), is("#,##0"));
        assertThat(metric.getMaqlAst().getPosition().getLine(), is(2));
        assertThat(metric.getMaqlAst().getPosition().getColumn(), is(1));
        assertThat(metric.getMaqlAst().getType(), is("metric"));
        assertThat(metric.getMaqlAst().getContent().length, is(1));
    }

    @Test
    public void testSerialization() throws Exception {
        final Metric metric = new Metric("Person Name", "SELECT SUM([/gdc/md/PROJECT_ID/obj/EXPR_ID])", "FORMAT");
        assertThat(metric, serializesToJson("/md/metric-new.json"));
    }

    @Test
    public void fullJsonShouldStayTheSameAfterDeserializationAndSerializationBack() throws Exception {
        final Metric metric = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/md/metric-out.json"), Metric.class);
        assertThat(metric, serializesToJson("/md/metric-out.json"));
    }
}