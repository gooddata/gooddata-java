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
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notEmpty;


/**
 * Represents {@link DateFilter} specifying relative range of given granularity.
 */
@JsonRootName(RelativeDateFilter.JSON_ROOT_NAME)
public class RelativeDateFilter extends DateFilter {

    private static final long serialVersionUID = 7257627800833737063L;
    static final String JSON_ROOT_NAME = "relativeDateFilter";

    private final String granularity;
    private final Integer from;
    private final Integer to;

    /**
     * Creates new instance
     * @param dataSet qualifier of date dimension dataSet
     * @param granularity granularity specified as type GDC date attribute type
     * @param from from
     * @param to to
     */
    @JsonCreator
    public RelativeDateFilter(@JsonProperty("dataSet") final ObjQualifier dataSet,
                              @JsonProperty("granularity") final String granularity,
                              @JsonProperty("from") final Integer from, @JsonProperty("to") final Integer to) {
        super(dataSet);
        this.granularity = notEmpty(granularity, "granularity");
        this.from = from;
        this.to = to;
    }

    public String getGranularity() {
        return granularity;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getTo() {
        return to;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new RelativeDateFilter(qualifier, granularity, from, to);
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
        RelativeDateFilter that = (RelativeDateFilter) o;
        return super.equals(that) && Objects.equals(granularity, that.granularity) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(granularity, from, to, super.hashCode());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
