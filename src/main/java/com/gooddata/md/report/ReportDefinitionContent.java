/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collection;

/**
 * Report definition content
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "format")
@JsonSubTypes({
        @JsonSubTypes.Type(name = GridReportDefinitionContent.FORMAT, value = GridReportDefinitionContent.class),
        @JsonSubTypes.Type(name = OneNumberReportDefinitionContent.FORMAT,
                value = OneNumberReportDefinitionContent.class)
        //TODO chart
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ReportDefinitionContent implements Serializable {

    private static final long serialVersionUID = -9027442077911353550L;
    private final String format;
    private final Grid grid;
    private final Collection<Filter> filters;

    public ReportDefinitionContent(String format, Grid grid, Collection<Filter> filters) {
        this.format = format;
        this.grid = grid;
        this.filters = filters;
    }

    @JsonIgnore // handled by type info
    public String getFormat() {
        return format;
    }

    public Grid getGrid() {
        return grid;
    }

    public Collection<Filter> getFilters() {
        return filters;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
