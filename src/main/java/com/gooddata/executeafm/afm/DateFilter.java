/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.executeafm.ObjQualifier;

import java.io.Serializable;
import java.util.Objects;

import static com.gooddata.util.Validate.notNull;

/**
 * Represents filter by date.
 */
public abstract class DateFilter implements FilterItem, Serializable {

    private static final long serialVersionUID = -804172518160419510L;
    private final ObjQualifier dataSet;

    /**
     * Creates new filter
     * @param dataSet qualifier of date dimension dataSet
     */
    DateFilter(final ObjQualifier dataSet) {
        this.dataSet = notNull(dataSet, "dataSet");
    }

    /**
     * @return filtered dataSet qualifier
     */
    @JsonProperty
    public ObjQualifier getDataSet() {
        return dataSet;
    }

    @Override
    public ObjQualifier getObjQualifier() {
        return getDataSet();
    }

    /**
     * @return true if no time period is specified, false otherwise
     */
    public abstract boolean isAllTimeSelected();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateFilter that = (DateFilter) o;
        return Objects.equals(dataSet, that.dataSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSet);
    }
}
