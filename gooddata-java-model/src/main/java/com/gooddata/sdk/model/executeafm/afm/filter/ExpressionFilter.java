/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Objects;

import static com.gooddata.sdk.model.executeafm.afm.filter.ExpressionFilter.JSON_ROOT_NAME;

/**
 * To be deprecated filter using plain expression
 */
@JsonRootName(JSON_ROOT_NAME)
public final class ExpressionFilter implements CompatibilityFilter {

    static final String JSON_ROOT_NAME = "expression";
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ExpressionFilter))
            return false;
        ExpressionFilter that = (ExpressionFilter) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
