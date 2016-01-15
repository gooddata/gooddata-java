/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Expression of fact
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
