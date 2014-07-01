/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grid content (in report definition)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Grid {

    private final Collection<String> columns;
    private final Collection<AttributeInGrid> rows;
    private final Collection<GridElement> metrics;
    private final Map<String, List<String>> sort;
    private final Collection<Map<String, Object>> columnWidths;

    @JsonCreator
    public Grid(@JsonProperty("columns") Collection<String> columns,
                @JsonProperty("rows") Collection<AttributeInGrid> rows,
                @JsonProperty("metrics") Collection<GridElement> metrics,
                @JsonProperty("sort") Map<String, List<String>> sort,
                @JsonProperty("columnWidths") Collection<Map<String, Object>> columnWidths) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
        this.sort = sort;
        this.columnWidths = columnWidths;
    }

    public Grid(final Collection<String> columns, final Collection<AttributeInGrid> rows,
                final Collection<GridElement> metrics) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
        sort = new LinkedHashMap<>();
        sort.put("columns", Collections.<String>emptyList());
        sort.put("rows", Collections.<String>emptyList());
        columnWidths = Collections.emptyList();
    }

    public Collection<String> getColumns() {
        return columns;
    }

    public Collection<AttributeInGrid> getRows() {
        return rows;
    }

    public Collection<GridElement> getMetrics() {
        return metrics;
    }

    public Collection<Map<String, Object>> getColumnWidths() {
        return columnWidths;
    }

    public Map<String, List<String>> getSort() {
        return sort;
    }
}
