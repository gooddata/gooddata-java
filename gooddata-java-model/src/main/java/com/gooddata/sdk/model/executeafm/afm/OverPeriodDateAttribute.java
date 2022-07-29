/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Definition of the {@link OverPeriodMeasureDefinition} attribute.
 */
public class OverPeriodDateAttribute implements Serializable {

    private static final long serialVersionUID = 2311364644023464059L;

    private final ObjQualifier attribute;
    private final Integer periodsAgo;

    /**
     * Create a new instance of {@link OverPeriodDateAttribute}.
     *
     * @param attribute
     *         The {@link ObjQualifier} of the attribute from the date data set that defines the PoP period and date data set. The parameter must not be
     *         null.
     * @param periodsAgo
     *         The number of periods defined by the {@code attribute} about which this period will be shifted about. The positive number shifts the period to
     *         the past, the negative to the future. The parameter must not be null.
     *
     * @throws IllegalArgumentException
     *         Thrown when one of the required parameter is null.
     */
    @JsonCreator
    public OverPeriodDateAttribute(
            @JsonProperty("attribute") final ObjQualifier attribute,
            @JsonProperty("periodsAgo") final Integer periodsAgo) {
        this.attribute = notNull(attribute, "attribute");
        this.periodsAgo = notNull(periodsAgo, "periodsAgo");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OverPeriodDateAttribute that = (OverPeriodDateAttribute) o;
        return Objects.equals(attribute, that.attribute) &&
                Objects.equals(periodsAgo, that.periodsAgo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, periodsAgo);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * The {@link ObjQualifier} of the attribute from the date data set that defines the PoP period.
     *
     * @return The date data set attribute that defines the PoP attribute.
     */
    public ObjQualifier getAttribute() {
        return attribute;
    }

    /**
     * The number of periods defined by the {@code attribute} about which this period will be shifted about. The positive number shifts the period to
     * the past, the negative to the future.
     *
     * @return The number of periods the data will be shifted about.
     */
    public Integer getPeriodsAgo() {
        return periodsAgo;
    }
}
