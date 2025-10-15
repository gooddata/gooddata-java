/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.sdk.model.md.Meta;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * One number report definition
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OneNumberReportDefinitionContent extends ReportDefinitionContent {

    private static final long serialVersionUID = 5479509323034916986L;
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
    private static class OneNumberVisualization implements Serializable {

        private static final long serialVersionUID = 1105233720917978784L;
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
    private static class OneNumberLabels implements Serializable {

        private static final long serialVersionUID = 6464599509495095669L;
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
        return create(title, columns, rows, metrics, Collections.emptyList());
    }

    public static ReportDefinition create(String title, List<GridElement> columns, List<GridElement> rows,
                                          List<MetricElement> metrics, Collection<Filter> filters) {
        return new ReportDefinition(new Meta(title), new OneNumberReportDefinitionContent(
                new Grid(columns, rows, metrics), title, filters));
    }
}

