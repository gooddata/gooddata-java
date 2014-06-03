/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * One number report definition
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OneNumberReportDefinitionContent extends ReportDefinitionContent {

    public static final String FORMAT = "oneNumber";

    private final OneNumberVisualization oneNumber;

    @JsonCreator
    private OneNumberReportDefinitionContent(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid,
                                             @JsonProperty("oneNumber") OneNumberVisualization oneNumber) {
        super(format, grid);
        this.oneNumber = oneNumber;
    }

    /* Just for serialization test */
    OneNumberReportDefinitionContent(Grid grid, String description) {
        super(FORMAT, grid);
        oneNumber = new OneNumberVisualization(new OneNumberLabels(description));
    }

    public String getFormat() {
        return FORMAT;
    }

    public OneNumberVisualization getOneNumber() {
        return oneNumber;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class OneNumberVisualization {

        private final OneNumberLabels labels;

        @JsonCreator
        private OneNumberVisualization(final OneNumberLabels labels) {
            this.labels = labels;
        }

        public OneNumberLabels getLabels() {
            return labels;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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
}
