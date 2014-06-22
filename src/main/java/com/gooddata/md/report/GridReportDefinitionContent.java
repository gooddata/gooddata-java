/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.Meta;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Grid report definition
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GridReportDefinitionContent extends ReportDefinitionContent {

    public static final String FORMAT = "grid";

    @JsonCreator
    private GridReportDefinitionContent(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid) {
        super(format, grid);
    }

    GridReportDefinitionContent(Grid grid) {
        this(FORMAT, grid);
    }

    public String getFormat() {
        return FORMAT;
    }

    public static ReportDefinition create(String title, List<String> columns, List<AttributeInGrid> rows,
                                          List<GridElement> metrics) {
        return new ReportDefinition(new Meta(title), new GridReportDefinitionContent(new Grid(columns, rows, metrics)));
    }
}
