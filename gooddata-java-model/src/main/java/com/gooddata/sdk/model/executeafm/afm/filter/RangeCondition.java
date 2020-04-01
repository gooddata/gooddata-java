/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Condition of {@link MeasureValueFilter} that compares measure values against two values.
 */
@JsonRootName(RangeCondition.NAME)
public class RangeCondition extends MeasureValueFilterCondition implements Serializable {

    static final String NAME = "range";

    private static final long serialVersionUID = -1219122779136638864L;

    private final String operator;
    private final BigDecimal from;
    private final BigDecimal to;

    @JsonCreator
    public RangeCondition(
        @JsonProperty("operator") final String operator,
        @JsonProperty("from") final BigDecimal from,
        @JsonProperty("to") final BigDecimal to,
        @JsonProperty("treatNullValuesAs") final BigDecimal treatNullValuesAs) {
        super(treatNullValuesAs);
        this.operator = notNull(operator, "operator");
        this.from = notNull(from, "from");
        this.to = notNull(to, "to");
    }

    /**
     * Creates a new {@link RangeCondition} instance.
     *
     * @param operator The operator of the range condition.
     * @param from     The left boundary value.
     * @param to       The right boundary value.
     */
    public RangeCondition(
        final RangeConditionOperator operator,
        final BigDecimal from,
        final BigDecimal to) {
        this(notNull(operator, "operator").toString(), from, to, null);
    }

    /**
     * Creates a new {@link RangeCondition} instance.
     *
     * @param operator          The operator of the range condition.
     * @param from              The left boundary value.
     * @param to                The right boundary value.
     * @param treatNullValuesAs The number that will be used instead of compared values that are null.
     */
    public RangeCondition(
        final RangeConditionOperator operator,
        final BigDecimal from,
        final BigDecimal to,
        final BigDecimal treatNullValuesAs) {
        this(notNull(operator, "operator").toString(), from, to, treatNullValuesAs);
    }

    /**
     * @return range condition operator
     */
    @JsonIgnore
    public RangeConditionOperator getOperator() {
        return RangeConditionOperator.of(operator);
    }

    @JsonProperty("operator")
    public String getStringOperator() { return this.operator; }

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
        if (!super.equals(o)) return false;
        final RangeCondition that = (RangeCondition) o;
        return Objects.equals(operator, that.operator) &&
            Objects.equals(from, that.from) &&
            Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operator, from, to);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
