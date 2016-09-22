/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.md.report.MetricGroup.METRIC_GROUP;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class GridTest {

    @Test
    public void testDeserialization() throws Exception {
        final Grid grid = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/md/report/grid.json"), Grid.class);
        assertThat(grid, is(notNullValue()));

        assertThat(grid.getColumns(), is(notNullValue()));
        assertThat(grid.getColumns(), hasSize(1));
        assertThat(grid.getColumns().iterator().next(), CoreMatchers.<GridElement>is(METRIC_GROUP));

        assertThat(grid.getRows(), is(notNullValue()));
        assertThat(grid.getRows(), hasSize(1));
        final AttributeInGrid attrInGrid = (AttributeInGrid) grid.getRows().iterator().next();
        assertThat(attrInGrid.getAlias(), is("attr"));

        assertThat(grid.getMetrics(), is(notNullValue()));
        assertThat(grid.getMetrics(), hasSize(1));

        assertThat(grid.getColumnWidths(), is(notNullValue()));
        assertThat(grid.getColumnWidths(), hasSize(1));

        final Map<String, Object> colWidth = grid.getColumnWidths().iterator().next();
        assertThat((Integer) colWidth.get("width"), is(343));
    }


    @Test
    public void testSerialization() throws Exception {
        final Map<String, Object> colWidths = new HashMap<>();
        final Map<String, List<String>> sort = new HashMap<>();
        colWidths.put("width", 343);
        sort.put("columns", Collections.<String>emptyList());
        sort.put("rows", Collections.<String>emptyList());
        final Grid grid = new Grid(
                asList(METRIC_GROUP),
                asList(new AttributeInGrid("/gdc/md/PROJECT_ID/obj/ATTR_ID", "attr")),
                asList(new MetricElement("/gdc/md/PROJECT_ID/obj/METR_ID", "metr")), sort, asList(colWidths));

        new ObjectMapper().writeValueAsString(grid);
        assertThat(grid, serializesToJson("/md/report/grid-input.json"));
    }

}