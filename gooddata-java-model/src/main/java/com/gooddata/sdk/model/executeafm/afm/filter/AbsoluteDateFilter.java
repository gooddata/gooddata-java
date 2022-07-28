/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.common.util.GDLocalDate;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents {@link DateFilter} specifying exact from and to dates.
 */
@JsonRootName(AbsoluteDateFilter.NAME)
public class AbsoluteDateFilter extends DateFilter {

    private static final long serialVersionUID = -1857726227400504182L;
    static final String NAME = "absoluteDateFilter";

    @GDLocalDate
    private final LocalDate from;
    @GDLocalDate
    private final LocalDate to;

    /**
     * Creates new filter instance
     * @param dataSet qualifier of date dimension dataset
     * @param from date from
     * @param to date to
     */
    @JsonCreator
    public AbsoluteDateFilter(@JsonProperty("dataSet") final ObjQualifier dataSet,
                              @JsonProperty("from") final LocalDate from,
                              @JsonProperty("to") final LocalDate to) {
        super(dataSet);
        this.from = from;
        this.to = to;
    }

    /**
     * @return date from
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * @return date to
     */
    public LocalDate getTo() {
        return to;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new AbsoluteDateFilter(qualifier, from, to);
    }

    @Override
    @JsonIgnore
    public boolean isAllTimeSelected() {
        return getFrom() == null || getTo() == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbsoluteDateFilter that = (AbsoluteDateFilter) o;
        return super.equals(that) && Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, super.hashCode());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
