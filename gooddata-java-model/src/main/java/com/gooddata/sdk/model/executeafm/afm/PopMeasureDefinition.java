/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static com.gooddata.sdk.model.executeafm.afm.PopMeasureDefinition.NAME;

/**
 * Definition of so called "period over period" measure.
 *
 * @deprecated Use {@link OverPeriodMeasureDefinition} with {@link OverPeriodDateAttribute#getPeriodsAgo()} set to {@code 1} instead.
 * Let's remove it once it's removed from API.
 */
@Deprecated
@JsonRootName(NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PopMeasureDefinition extends DerivedMeasureDefinition {

    private static final long serialVersionUID = 1430640153994197345L;

    static final String NAME = "popMeasure";

    private final ObjQualifier popAttribute;

    /**
     * Creates new definition from given measure identifier referencing another measure in {@link Afm} and given attribute qualifier (should qualify date
     * attribute)
     *
     * @param measureIdentifier
     *         measure identifier
     * @param popAttribute
     *         "period over period" date attribute
     */
    @JsonCreator
    public PopMeasureDefinition(@JsonProperty("measureIdentifier") final String measureIdentifier,
                                @JsonProperty("popAttribute") final ObjQualifier popAttribute) {
        super(measureIdentifier);
        this.popAttribute = popAttribute;
    }

    @Override
    public MeasureDefinition withObjUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        return ObjIdentifierUtilities.copyIfNecessary(
                this,
                popAttribute,
                uriObjQualifier -> new PopMeasureDefinition(measureIdentifier, uriObjQualifier),
                objQualifierConverter
        );
    }

    /**
     * Determine if measure is ad-hoc
     *
     * @return always true (PopMeasure is always ad-hoc)
     */
    @Override
    public boolean isAdHoc() {
        return true;
    }

    public ObjQualifier getPopAttribute() {
        return popAttribute;
    }

    @Override
    public Collection<ObjQualifier> getObjQualifiers() {
        return popAttribute == null
                ? Collections.emptySet()
                : Collections.singleton(popAttribute);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final PopMeasureDefinition that = (PopMeasureDefinition) o;
        return Objects.equals(popAttribute, that.popAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), popAttribute);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
