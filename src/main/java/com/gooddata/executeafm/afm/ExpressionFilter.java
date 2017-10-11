/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * To be deprecated filter using plain expression
 */
@JsonRootName("expression")
public class ExpressionFilter implements CompatibilityFilter {
    static final String NAME = "expression";
    private final String value;

    /**
     * Creates new instance
     * @param value expression value
     */
    @JsonCreator
    public ExpressionFilter(@JsonProperty("value") final String value) {
        this.value = value;
    }

   public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
