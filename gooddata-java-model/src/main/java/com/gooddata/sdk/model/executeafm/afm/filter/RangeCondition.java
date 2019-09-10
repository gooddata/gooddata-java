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
 * Condition of {@link MeasureValueFilter} that compares measure values against two values.
 */
@JsonRootName(RangeCondition.NAME)
public class RangeCondition implements MeasureValueFilterCondition, Serializable {

    static final String NAME = "range";

    private static final long serialVersionUID = -1219122779136638864L;

    private final RangeConditionOperator operator;
    private final BigDecimal from;
    private final BigDecimal to;

    /**
     * Creates a new {@link RangeCondition} instance.
     *
     * @param operator The operator of the range condition.
     * @param from The left boundary value.
     * @param to The right boundary value.
     */
    @JsonCreator
    public RangeCondition(
            @JsonProperty("operator") final RangeConditionOperator operator,
            @JsonProperty("from") final BigDecimal from,
            @JsonProperty("to") final BigDecimal to) {
        this.operator = operator;
        this.from = from;
        this.to = to;
    }

    /**
     * @return range condition operator
     */
    public RangeConditionOperator getOperator() {
        return operator;
    }

    /**
     * @return left boundary of the range
     */
    public BigDecimal getFrom() {
        return from;
    }

    /**
     * @return right boundary of the range
     */
    public BigDecimal getTo() {
        return to;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RangeCondition that = (RangeCondition) o;
        return operator == that.operator &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, from, to);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
