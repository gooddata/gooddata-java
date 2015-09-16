/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.gooddata.md.Meta;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * One number report definition
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

    public static ReportDefinition create(String title, List<String> columns, List<AttributeInGrid> rows,
                                          List<GridElement> metrics) {
        return create(title, columns, rows, metrics, Collections.<Filter>emptyList());
    }

    public static ReportDefinition create(String title, List<String> columns, List<AttributeInGrid> rows,
                                          List<GridElement> metrics, Collection<Filter> filters) {
        return new ReportDefinition(new Meta(title), new OneNumberReportDefinitionContent(
                new Grid(columns, rows, metrics), title, filters));
    }
}
