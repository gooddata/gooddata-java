/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * Report definition content
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "format")
@JsonSubTypes({
        @JsonSubTypes.Type(name = GridReportDefinitionContent.FORMAT, value = GridReportDefinitionContent.class),
        @JsonSubTypes.Type(name = OneNumberReportDefinitionContent.FORMAT,
                value = OneNumberReportDefinitionContent.class)
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public abstract class ReportDefinitionContent {

    private final String format;
    private final Grid grid;
    private final Collection<Object> filters; //TODO proper type

    public ReportDefinitionContent(String format, Grid grid) {
        this.format = format;
        this.grid = grid;
        this.filters = emptyList();
    }

    @JsonIgnore // handled by type info
    public String getFormat() {
        return format;
    }

    public Grid getGrid() {
        return grid;
    }

    public Collection<Object> getFilters() {
        return filters;
    }
}
