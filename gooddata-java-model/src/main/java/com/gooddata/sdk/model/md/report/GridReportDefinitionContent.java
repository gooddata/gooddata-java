/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.md.Meta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Grid report definition
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GridReportDefinitionContent extends ReportDefinitionContent {

    public static final String FORMAT = "grid";
    private static final long serialVersionUID = 1296467241365069724L;

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
        this(grid, Collections.emptyList());
    }

    public static ReportDefinition create(String title, List<? extends GridElement> columns, List<? extends GridElement> rows,
                                          List<MetricElement> metrics) {
        return create(title, columns, rows, metrics, Collections.emptyList());
    }

    public static ReportDefinition create(String title, List<? extends GridElement> columns, List<? extends GridElement> rows,
                                          List<MetricElement> metrics, Collection<Filter> filters) {
        return new ReportDefinition(new Meta(title), new GridReportDefinitionContent(new Grid(columns, rows, metrics), filters));
    }

    public String getFormat() {
        return FORMAT;
    }
}
