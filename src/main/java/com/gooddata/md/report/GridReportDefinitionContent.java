/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.Meta;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Grid report definition
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GridReportDefinitionContent extends ReportDefinitionContent {

    public static final String FORMAT = "grid";

    @JsonCreator
    private GridReportDefinitionContent(
            @JsonProperty("format") String format,
            @JsonProperty("grid") Grid grid,
            @JsonProperty("filters") Collection<Filter> filters) {
        super(format, grid, filters);
    }

    GridReportDefinitionContent(Grid grid, Collection<Filter> filters) {
        this(FORMAT, grid, filters);
    }

    GridReportDefinitionContent(Grid grid) {
        this(grid, Collections.<Filter>emptyList());
    }

    public String getFormat() {
        return FORMAT;
    }

    public static ReportDefinition create(String title, List<String> columns, List<AttributeInGrid> rows,
                                          List<GridElement> metrics) {
        return create(title, columns, rows, metrics, Collections.<Filter>emptyList());
    }

    public static ReportDefinition create(String title, List<String> columns, List<AttributeInGrid> rows,
                                          List<GridElement> metrics, Collection<Filter> filters) {
        return new ReportDefinition(new Meta(title), new GridReportDefinitionContent(new Grid(columns, rows, metrics), filters));
    }
}
