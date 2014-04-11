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
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grid {

    private final Collection<String> columns;
    private final Collection<AttributeItem> rows;
    private final Collection<Item> metrics;
    private final Map<String,List<String>> sort;
    private final Collection<String> columnWidths = Collections.emptyList();

    @JsonCreator
    public Grid(@JsonProperty("columns") Collection<String> columns, @JsonProperty("rows") Collection<AttributeItem> rows,
                @JsonProperty("metrics") Collection<Item> metrics) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
        sort = new LinkedHashMap<>();
        sort.put("columns", Collections.<String>emptyList());
        sort.put("rows", Collections.<String>emptyList());
    }

    public Collection<String> getColumns() {
        return columns;
    }

    public Collection<AttributeItem> getRows() {
        return rows;
    }

    public Collection<Item> getMetrics() {
        return metrics;
    }

    public Collection<String> getColumnWidths() {
        return columnWidths;
    }

    public Map<String, List<String>> getSort() {
        return sort;
    }
}
