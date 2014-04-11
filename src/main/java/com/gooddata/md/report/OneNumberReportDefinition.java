/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OneNumberReportDefinition extends ReportDefinitionContent {

    public static final String FORMAT = "oneNumber";

    private final Grid grid;
    private final Map<String, Map<String, String>> oneNumber;

    @JsonCreator
    public OneNumberReportDefinition(@JsonProperty("format") String format, @JsonProperty("grid") Grid grid,
                                     @JsonProperty("oneNumber") Map<String, Map<String, String>> oneNumber) {
        super(format);
        this.grid = grid;
        this.oneNumber = oneNumber;
    }

}
