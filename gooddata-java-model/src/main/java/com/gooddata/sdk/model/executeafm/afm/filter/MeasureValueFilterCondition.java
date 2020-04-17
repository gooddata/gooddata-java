/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Covers all the conditions that can be used within {@link MeasureValueFilter}.
 * 
 * Contains shared functionality to set a custom value instead of {@code null} measure
 * values against the condition's value will be compared to.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ComparisonCondition.class, name = ComparisonCondition.NAME),
        @JsonSubTypes.Type(value = RangeCondition.class, name = RangeCondition.NAME)
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class MeasureValueFilterCondition implements Serializable {
    private static final long serialVersionUID = 7845069496955848265L;

    private final BigDecimal treatNullValuesAs;

    public MeasureValueFilterCondition(final BigDecimal treatNullValuesAs) {
        this.treatNullValuesAs = treatNullValuesAs;
    }

    @JsonProperty
    public BigDecimal getTreatNullValuesAs() {
        return treatNullValuesAs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MeasureValueFilterCondition that = (MeasureValueFilterCondition) o;
        return Objects.equals(treatNullValuesAs, that.treatNullValuesAs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatNullValuesAs);
    }
}
