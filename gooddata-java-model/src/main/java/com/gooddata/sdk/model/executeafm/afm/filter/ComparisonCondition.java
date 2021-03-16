/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Condition of {@link MeasureValueFilter} that compares measure values against a single value.
 */
@JsonRootName(ComparisonCondition.JSON_ROOT_NAME)
public class ComparisonCondition extends MeasureValueFilterCondition implements Serializable {

    static final String JSON_ROOT_NAME = "comparison";

    private static final long serialVersionUID = 2944349621407799356L;
    private static final String OPERATOR_FIELD_NAME = "operator";

    private final String operator;
    private final BigDecimal value;

    @JsonCreator
    public ComparisonCondition(
        @JsonProperty(OPERATOR_FIELD_NAME) final String operator,
        @JsonProperty("value") final BigDecimal value,
        @JsonProperty("treatNullValuesAs") final BigDecimal treatNullValuesAs
    ) {
        super(treatNullValuesAs);
        this.operator = notNull(operator, OPERATOR_FIELD_NAME);
        this.value = notNull(value, "value");
    }

    /**
     * Creates a new {@link ComparisonCondition} instance.
     *
     * @param operator The operator of the condition .
     * @param value    The value of the condition.
     */
    public ComparisonCondition(
        final ComparisonConditionOperator operator,
        final BigDecimal value
    ) {
        this(notNull(operator, OPERATOR_FIELD_NAME).toString(), value, null);
    }

    /**
     * Creates a new {@link ComparisonCondition} instance.
     *
     * @param operator          The operator of the condition .
     * @param value             The value of the condition.
     * @param treatNullValuesAs The number that will be used instead of compared values that are null.
     */
    public ComparisonCondition(
        final ComparisonConditionOperator operator,
        final BigDecimal value,
        final BigDecimal treatNullValuesAs
    ) {
        this(notNull(operator, OPERATOR_FIELD_NAME).toString(), value, treatNullValuesAs);
    }

    /**
     * @return comparison condition operator
     */
    @JsonIgnore
    public ComparisonConditionOperator getOperator() {
        return ComparisonConditionOperator.of(operator);
    }

    @JsonProperty(OPERATOR_FIELD_NAME)
    public String getStringOperator() {
        return this.operator;
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
        if (!super.equals(o)) return false;
        final ComparisonCondition that = (ComparisonCondition) o;
        return Objects.equals(operator, that.operator) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), operator, value);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
