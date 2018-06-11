/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

import static com.gooddata.util.Validate.notNull;

/**
 * Definition of the {@link PreviousPeriodMeasureDefinition} data set.
 */
public class PreviousPeriodDateDataSet implements Serializable {

    private static final long serialVersionUID = 2311364644023464059L;

    private final ObjQualifier dataSet;
    private final Integer periodsAgo;

    /**
     * Create a new instance of {@link PreviousPeriodDateDataSet}.
     *
     * @param dataSet
     *         The {@link ObjQualifier} of the data set that match one of the {@link DateFilter} in the execution. The parameter must not be null.
     * @param periodsAgo
     *         The number of periods defined by the matching date filter which this period will be shifted about. The positive number shifts the period to
     *         the past, the negative to the future. The parameter must not be null.
     *
     * @throws IllegalArgumentException
     *         Thrown when one of the required parameter is null.
     */
    @JsonCreator
    public PreviousPeriodDateDataSet(
            @JsonProperty("dataSet") final ObjQualifier dataSet,
            @JsonProperty("periodsAgo") final Integer periodsAgo) {
        this.dataSet = notNull(dataSet, "dataSet");
        this.periodsAgo = notNull(periodsAgo, "periodsAgo");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PreviousPeriodDateDataSet that = (PreviousPeriodDateDataSet) o;
        return Objects.equals(dataSet, that.dataSet) &&
                Objects.equals(periodsAgo, that.periodsAgo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSet, periodsAgo);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    /**
     * The {@link ObjQualifier} of the data set that match one of the {@link DateFilter} in the execution.
     *
     * @return The data set for matching of the AFM filter.
     */
    public ObjQualifier getDataSet() {
        return dataSet;
    }

    /**
     * The number of periods defined by the matching date filter which this period will be shifted about. The positive number shifts the period to
     * the past, the negative to the future.
     *
     * @return The number of periods the data will be shifted about.
     */
    public Integer getPeriodsAgo() {
        return periodsAgo;
    }
}
