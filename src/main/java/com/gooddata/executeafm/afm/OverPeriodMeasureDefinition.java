/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gooddata.executeafm.afm.OverPeriodMeasureDefinition.NAME;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Definition of the period over period measure that is used for the Same period last year and Same period 2 years back comparisons.
 */
@JsonRootName(NAME)
public class OverPeriodMeasureDefinition extends DerivedMeasureDefinition implements Serializable {

    private static final long serialVersionUID = -8904516814279504098L;

    static final String NAME = "overPeriodMeasure";

    private final List<OverPeriodDateAttribute> dateAttributes;

    /**
     * Create a new instance of {@link OverPeriodMeasureDefinition}.
     *
     * @param measureIdentifier
     *         The local identifier of the measure this PoP measure refers to. The parameter must not be null.
     * @param dateAttributes
     *         The date attributes that defines how this measure will be shifted in time. The parameter must not be null.
     *
     * @throws IllegalArgumentException
     *         Thrown when {@code dateAttributes} list is empty or required parameter is null.
     */
    @JsonCreator
    public OverPeriodMeasureDefinition(
            @JsonProperty("measureIdentifier") final String measureIdentifier,
            @JsonProperty("dateAttributes") final List<OverPeriodDateAttribute> dateAttributes) {
        super(measureIdentifier);
        this.dateAttributes = notEmpty(dateAttributes, "dateAttributes");
    }

    /**
     * The method is not supported by the object.
     * Use {@link #getObjQualifiers()} instead.
     *
     * @return throws {@link UnsupportedOperationException}
     *
     * @throws UnsupportedOperationException
     *         The exception is thrown every time the method is called.
     * @deprecated Use {@link #getObjQualifiers()} instead.
     */
    @Override
    @Deprecated
    public ObjQualifier getObjQualifier() {
        throw new UnsupportedOperationException("The method is not supported by the object!");
    }

    @Override
    public Collection<ObjQualifier> getObjQualifiers() {
        return this.dateAttributes.stream()
                .map(OverPeriodDateAttribute::getAttribute)
                .collect(Collectors.toSet());
    }

    /**
     * The method is not supported by the object.
     * Use {@link #withObjUriQualifiers(ObjQualifierConverter)} instead.
     *
     * @param uriQualifier
     *         The URI object qualifier.
     *
     * @return throws {@link UnsupportedOperationException}
     *
     * @throws UnsupportedOperationException
     *         The exception is thrown every time the method is called.
     * @deprecated Use {@link #withObjUriQualifiers(ObjQualifierConverter)} instead.
     */
    @Override
    @Deprecated
    public MeasureDefinition withObjUriQualifier(final UriObjQualifier uriQualifier) {
        throw new UnsupportedOperationException("The method is not supported by the object!");
    }

    @Override
    public MeasureDefinition withObjUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        notNull(objQualifierConverter, "objQualifierConverter");
        return new OverPeriodMeasureDefinition(measureIdentifier, copyAttributesWithUriQualifiers(objQualifierConverter));
    }

    private List<OverPeriodDateAttribute> copyAttributesWithUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        return dateAttributes.stream()
                .map(attribute -> copyWithUriQualifier(attribute, objQualifierConverter))
                .collect(Collectors.toList());
    }

    private OverPeriodDateAttribute copyWithUriQualifier(final OverPeriodDateAttribute attribute, final ObjQualifierConverter objQualifierConverter) {
        return ObjIdentifierUtilities.copyIfNecessary(
                attribute,
                attribute.getAttribute(),
                uriObjQualifier -> new OverPeriodDateAttribute(uriObjQualifier, attribute.getPeriodsAgo()),
                objQualifierConverter
        );
    }

    /**
     * Determine if measure is ad-hoc, i.e., if it does not exist in the catalog and was created on fly.
     *
     * @return always true ({@link OverPeriodMeasureDefinition} is always ad-hoc)
     */
    @Override
    public boolean isAdHoc() {
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final OverPeriodMeasureDefinition that = (OverPeriodMeasureDefinition) o;
        return Objects.equals(dateAttributes, that.dateAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateAttributes);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * The date attributes that defines how this measure will be shifted in time.
     *
     * @return The list of date attributes.
     */
    public List<OverPeriodDateAttribute> getDateAttributes() {
        return dateAttributes;
    }
}
