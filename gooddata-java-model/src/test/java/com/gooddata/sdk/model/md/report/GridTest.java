/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.gooddata.sdk.model.md.Metric;
import org.apache.commons.lang3.SerializationUtils;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gooddata.sdk.model.md.report.MetricGroup.METRIC_GROUP;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class GridTest {

    @Test
    public void testDeserialization() throws Exception {
        final Grid grid = readObjectFromResource("/md/report/grid.json", Grid.class);
        assertThat(grid, is(notNullValue()));

        assertThat(grid.getColumns(), is(notNullValue()));
        assertThat(grid.getColumns(), hasSize(1));
        assertThat(grid.getColumns().iterator().next(), CoreMatchers.is(METRIC_GROUP));

        assertThat(grid.getRows(), is(notNullValue()));
        assertThat(grid.getRows(), hasSize(1));
        final AttributeInGrid attrInGrid = (AttributeInGrid) grid.getRows().iterator().next();
        assertThat(attrInGrid.getAlias(), is("attr"));

        assertThat(grid.getMetrics(), is(notNullValue()));
        assertThat(grid.getMetrics(), hasSize(1));

        assertThat(grid.getColumnWidths(), is(notNullValue()));
        assertThat(grid.getColumnWidths(), hasSize(1));

        final Map<String, Object> colWidth = grid.getColumnWidths().iterator().next();
        assertThat(colWidth.get("width"), is(343));
    }


    @Test
    public void testSerialization() throws Exception {
        final Map<String, Object> colWidths = new HashMap<>();
        final Map<String, List<String>> sort = new HashMap<>();
        colWidths.put("width", 343);
        sort.put("columns", Collections.emptyList());
        sort.put("rows", Collections.emptyList());
        final Grid grid = new Grid(
                asList(METRIC_GROUP),
                asList(new AttributeInGrid("/gdc/md/PROJECT_ID/obj/ATTR_ID", "attr")),
                asList(new MetricElement(readObjectFromResource("/md/metric-out.json", Metric.class), "metr")),
                sort,
                asList(colWidths)
        );

        assertThat(grid, jsonEquals(resource("md/report/grid-input.json")));
    }


    @Test
    public void testToStringFormat() throws Exception {
        final Grid grid = readObjectFromResource("/md/report/grid.json", Grid.class);

        assertThat(grid.toString(), matchesPattern(Grid.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Grid grid = readObjectFromResource("/md/report/grid.json", Grid.class);
        final Grid deserialized = SerializationUtils.roundtrip(grid);

        assertThat(deserialized, jsonEquals(grid));
    }

}