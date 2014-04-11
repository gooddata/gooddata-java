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
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GridReportDefinition extends ReportDefinitionContent {

    public static final String FORMAT = "grid";

    private final Grid grid;

    @JsonCreator
    public GridReportDefinition(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid) {
        super(format);
        this.grid = grid;
    }

    public GridReportDefinition(Grid grid) {
        this(FORMAT, grid);
    }

    public Grid getGrid() {
        return grid;
    }

    public static ReportDefinition create(String title, List<String> columns, List<AttributeItem> rows, List<Item> metrics) {
        return new ReportDefinition(new Meta(title), new GridReportDefinition(new Grid(columns, rows, metrics)));
    }
}
