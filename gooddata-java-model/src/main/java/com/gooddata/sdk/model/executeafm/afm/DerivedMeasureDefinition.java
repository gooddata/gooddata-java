/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * The superclass of the {@link MeasureDefinition} classes that are derived from the master measure and have the identifier of the master measure.
 */
@SuppressWarnings("deprecation")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PopMeasureDefinition.class, name = PopMeasureDefinition.JSON_ROOT_NAME),
        @JsonSubTypes.Type(value = OverPeriodMeasureDefinition.class, name = OverPeriodMeasureDefinition.JSON_ROOT_NAME),
        @JsonSubTypes.Type(value = PreviousPeriodMeasureDefinition.class, name = PreviousPeriodMeasureDefinition.JSON_ROOT_NAME)
})
public abstract class DerivedMeasureDefinition implements MeasureDefinition {

    private static final long serialVersionUID = -1203802872091017113L;

    protected final String measureIdentifier;

    /**
     * Create a new instance of {@link DerivedMeasureDefinition}.
     *
     * @param measureIdentifier
     *         The local identifier of the master measure this derived measure refers to. The parameter must not be null.
     *
     * @throws IllegalArgumentException
     *         Thrown when required parameter is null.
     */
    DerivedMeasureDefinition(final String measureIdentifier) {
        this.measureIdentifier = notNull(measureIdentifier, "measureIdentifier");
    }

    /**
     * The local identifier of the master measure this derived measure refers to.
     *
     * @return The local identifier of the master measure.
     */
    @JsonProperty
    public String getMeasureIdentifier() {
        return measureIdentifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DerivedMeasureDefinition that = (DerivedMeasureDefinition) o;
        return Objects.equals(measureIdentifier, that.measureIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measureIdentifier);
    }
}
