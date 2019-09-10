/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Condition of {@link MeasureValueFilter} that compares measure values against a single value.
 */
@JsonRootName(ComparisonCondition.NAME)
public class ComparisonCondition implements MeasureValueFilterCondition, Serializable {

    static final String NAME = "comparison";

    private static final long serialVersionUID = 2944349621407799356L;

    private final ComparisonConditionOperator operator;
    private final BigDecimal value;

    /**
     * Creates a new {@link ComparisonCondition} instance.
     *
     * @param operator The operator of the condition .
     * @param value The value of the condition.
     */
    @JsonCreator
    public ComparisonCondition(
            @JsonProperty("operator") final ComparisonConditionOperator operator,
            @JsonProperty("value") final BigDecimal value
    ) {
        this.operator = operator;
        this.value = value;
    }

    /**
     * @return comparison condition operator
     */
    public ComparisonConditionOperator getOperator() {
        return operator;
    }

    /**
     * @return comparison condition value
     */
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ComparisonCondition that = (ComparisonCondition) o;
        return operator == that.operator &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, value);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
