/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Grid element
 * (metricGroup | URI | AttributeInGrid ... URI is allowed for backward compatibility, metricGroup can be maximally
 * one time in rows or columns)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GridElement {

    private final String uri;
    private final String alias;

    @JsonCreator
    public GridElement(@JsonProperty("uri") String uri, @JsonProperty("alias") String alias) {
        this.uri = uri;
        this.alias = alias;
    }

    public String getUri() {
        return uri;
    }

    public String getAlias() {
        return alias;
    }

}
