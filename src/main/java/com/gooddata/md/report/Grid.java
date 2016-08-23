/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grid content (in report definition)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Grid {

    private final List<? extends GridElement> columns;
    private final List<? extends GridElement> rows;
    private final List<MetricElement> metrics;
    private final Map<String, List<String>> sort;
    private final Collection<Map<String, Object>> columnWidths;

    /**
     * Creates new instance.
     * @param columns report's definition columns
     * @param rows report's definition rows
     * @param metrics report's definition metrics
     * @param sort report's sort definition
     * @param columnWidths report columns' widths definition
     *
     * @since 2.0.0
     */
    @JsonCreator
    public Grid(@JsonProperty("columns") @JsonDeserialize(contentUsing = GridElementDeserializer.class)
                        List<? extends GridElement> columns,
                @JsonProperty("rows")  @JsonDeserialize(contentUsing = GridElementDeserializer.class)
                        List<? extends GridElement> rows,
                @JsonProperty("metrics") List<MetricElement> metrics,
                @JsonProperty("sort") Map<String, List<String>> sort,
                @JsonProperty("columnWidths") Collection<Map<String, Object>> columnWidths) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
        this.sort = sort;
        this.columnWidths = columnWidths;
    }

    public Grid(List<? extends GridElement> columns, List<? extends GridElement> rows, List<MetricElement> metrics) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
        sort = new LinkedHashMap<>();
        sort.put("columns", Collections.<String>emptyList());
        sort.put("rows", Collections.<String>emptyList());
        columnWidths = Collections.emptyList();
    }

    @JsonSerialize(contentUsing = GridElementSerializer.class)
    public List<? extends GridElement> getColumns() {
        return columns;
    }

    @JsonSerialize(contentUsing = GridElementSerializer.class)
    public List<? extends GridElement> getRows() {
        return rows;
    }

    public List<MetricElement> getMetrics() {
        return metrics;
    }

    public Collection<Map<String, Object>> getColumnWidths() {
        return columnWidths;
    }

    public Map<String, List<String>> getSort() {
        return sort;
    }
}
