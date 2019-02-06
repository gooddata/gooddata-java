/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;

/**
 * Expression of fact
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Expression implements Serializable {

    private static final long serialVersionUID = 9161488874222662015L;
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
