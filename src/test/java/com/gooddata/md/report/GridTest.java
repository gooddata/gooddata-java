/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class GridTest {

    @Test
    public void testDeserialization() throws Exception {
        final Grid grid = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/md/report/grid.json"), Grid.class);
        assertThat(grid, is(notNullValue()));

        assertThat(grid.getColumns(), is(notNullValue()));
        assertThat(grid.getColumns(), hasSize(1));
        assertThat(grid.getColumns().iterator().next(), is("metricGroup"));

        assertThat(grid.getRows(), is(notNullValue()));
        assertThat(grid.getRows(), hasSize(1));

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
        final Grid grid = new Grid(asList("metricGroup"), Collections.<AttributeInGrid>emptyList(),
                asList(new GridElement("/gdc/md/PROJECT_ID/obj/METR_ID", "metr")), sort, asList(colWidths));

        assertThat(grid, serializesToJson("/md/report/grid-input.json"));
    }

}