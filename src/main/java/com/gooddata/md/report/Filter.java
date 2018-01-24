/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;

import static com.gooddata.util.Validate.notEmpty;

/**
 * Filter (in report definition)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Filter implements Serializable {

    private static final long serialVersionUID = 5776136459952322123L;
    private final String expression;

    @JsonCreator
    public Filter(@JsonProperty("expression") final String expression) {
        this.expression = notEmpty(expression, "expression");
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
