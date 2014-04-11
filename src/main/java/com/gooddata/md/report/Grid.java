/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Grid {

    private final Collection<String> columns;
    private final Collection<AttributeItem> rows;
    private final Collection<Item> metrics;

    @JsonCreator
    public Grid(@JsonProperty("columns") Collection<String> columns, @JsonProperty("rows") Collection<AttributeItem> rows,
                @JsonProperty("metrics") Collection<Item> metrics) {
        this.columns = columns;
        this.rows = rows;
        this.metrics = metrics;
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
}
