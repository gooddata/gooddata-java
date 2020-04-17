/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.gooddata.sdk.model.executeafm.afm.PreviousPeriodMeasureDefinition.NAME;
import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Definition of the period over period measure that is used for the Previous period comparison.
 */
@JsonRootName(NAME)
public class PreviousPeriodMeasureDefinition extends DerivedMeasureDefinition {

    private static final long serialVersionUID = -4741355657671354062L;

    static final String NAME = "previousPeriodMeasure";

    private final List<PreviousPeriodDateDataSet> dateDataSets;

    /**
     * Create a new instance of {@link PreviousPeriodMeasureDefinition}.
     *
     * @param measureIdentifier
     *         The local identifier of the measure this PoP measure refers to. The parameter must not be null.
     * @param dateDataSets
     *         The date data sets that defines how this measure will be shifted in time. The parameter must not be null.
     *
     * @throws IllegalArgumentException
     *         Thrown when {@code attributes} list is empty or required parameter is null.
     */
    @JsonCreator
    public PreviousPeriodMeasureDefinition(
            @JsonProperty("measureIdentifier") final String measureIdentifier,
            @JsonProperty("dateDataSets") final List<PreviousPeriodDateDataSet> dateDataSets) {
        super(measureIdentifier);
        this.dateDataSets = notEmpty(dateDataSets, "dateDataSets");
    }

    @Override
    public Collection<ObjQualifier> getObjQualifiers() {
        return this.dateDataSets.stream()
                .map(PreviousPeriodDateDataSet::getDataSet)
                .collect(Collectors.toSet());
    }

    @Override
    public MeasureDefinition withObjUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        notNull(objQualifierConverter, "objQualifierConverter");
        return new PreviousPeriodMeasureDefinition(measureIdentifier, copyDataSetsWithUriQualifiers(objQualifierConverter));
    }

    private List<PreviousPeriodDateDataSet> copyDataSetsWithUriQualifiers(final ObjQualifierConverter objQualifierConverter) {
        return dateDataSets.stream()
                .map(dataSet -> copyWithUriQualifier(dataSet, objQualifierConverter))
                .collect(Collectors.toList());
    }

    private PreviousPeriodDateDataSet copyWithUriQualifier(final PreviousPeriodDateDataSet dataSet, final ObjQualifierConverter objQualifierConverter) {
        return ObjIdentifierUtilities.copyIfNecessary(
                dataSet,
                dataSet.getDataSet(),
                uriObjQualifier -> new PreviousPeriodDateDataSet(uriObjQualifier, dataSet.getPeriodsAgo()),
                objQualifierConverter
        );
    }

    /**
     * Determine if measure is ad-hoc, i.e., if it does not exist in the catalog and was created on fly.
     *
     * @return always true ({@link PreviousPeriodMeasureDefinition} is always ad-hoc)
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
        final PreviousPeriodMeasureDefinition that = (PreviousPeriodMeasureDefinition) o;
        return Objects.equals(dateDataSets, that.dateDataSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateDataSets);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * The date data sets that defines how this measure will be shifted in time.
     *
     * @return The list of date data sets.
     */
    public List<PreviousPeriodDateDataSet> getDateDataSets() {
        return dateDataSets;
    }
}
