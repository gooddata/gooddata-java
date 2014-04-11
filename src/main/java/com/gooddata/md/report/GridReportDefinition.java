/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GridReportDefinition extends ReportDefinitionContent {

    private final Grid grid;

    @JsonCreator
    public GridReportDefinition(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid) {
        super(format);
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }
}
