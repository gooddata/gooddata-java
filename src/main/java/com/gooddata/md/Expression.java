/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Expression of fact
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Expression {

    private final String data;
    private final String type;

    @JsonCreator
    public Expression(@JsonProperty("data") String data, @JsonProperty("type") String type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
