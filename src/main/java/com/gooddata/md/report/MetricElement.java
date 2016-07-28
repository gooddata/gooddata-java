/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetricElement {

    private final String uri;
    private final String alias;

    @JsonCreator
    public MetricElement(@JsonProperty("uri") final String uri,
                         @JsonProperty("alias") final String alias) {
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
