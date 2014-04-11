/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import java.util.Collection;
import java.util.Collections;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "format")
@JsonSubTypes({
        @JsonSubTypes.Type(name = GridReportDefinition.FORMAT, value = GridReportDefinition.class),
        @JsonSubTypes.Type(name = OneNumberReportDefinition.FORMAT, value = OneNumberReportDefinition.class)
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ReportDefinitionContent {

    private final String format;
    private final Collection<String> filters = Collections.emptyList();

    public ReportDefinitionContent(String format) {
        this.format = format;
    }

    public Collection<String> getFilters() {
        return filters;
    }

    @JsonIgnore // handled by type info
    public String getFormat() {
        return format;
    }
}
