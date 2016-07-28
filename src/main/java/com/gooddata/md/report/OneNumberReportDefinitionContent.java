/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.md.Meta;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * One number report definition
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OneNumberReportDefinitionContent extends ReportDefinitionContent {

    public static final String FORMAT = "oneNumber";

    private final OneNumberVisualization oneNumber;

    @JsonCreator
    private OneNumberReportDefinitionContent(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid,
                                             @JsonProperty("oneNumber") OneNumberVisualization oneNumber,
                                             @JsonProperty("filters") Collection<Filter> filters) {
        super(format, grid, filters);
        this.oneNumber = oneNumber;
    }

    /* Just for serialization test */
    OneNumberReportDefinitionContent(Grid grid, String description, Collection<Filter> filters) {
        super(FORMAT, grid, filters);
        oneNumber = new OneNumberVisualization(new OneNumberLabels(description));
    }

    public String getFormat() {
        return FORMAT;
    }

    public OneNumberVisualization getOneNumber() {
        return oneNumber;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class OneNumberVisualization {

        private final OneNumberLabels labels;

        @JsonCreator
        private OneNumberVisualization(@JsonProperty("labels") final OneNumberLabels labels) {
            this.labels = labels;
        }

        public OneNumberLabels getLabels() {
            return labels;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class OneNumberLabels {

        private final String description;

        @JsonCreator
        private OneNumberLabels(@JsonProperty("description") String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static ReportDefinition create(String title, List<GridElement> columns, List<GridElement> rows,
                                          List<MetricElement> metrics) {
        return create(title, columns, rows, metrics, Collections.<Filter>emptyList());
    }

    public static ReportDefinition create(String title, List<GridElement> columns, List<GridElement> rows,
                                          List<MetricElement> metrics, Collection<Filter> filters) {
        return new ReportDefinition(new Meta(title), new OneNumberReportDefinitionContent(
                new Grid(columns, rows, metrics), title, filters));
    }
}
