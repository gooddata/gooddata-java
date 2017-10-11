/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
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

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Represents {@link DateFilter} specifying relative range of given granularity.
 */
@JsonRootName(RelativeDateFilter.NAME)
public class RelativeDateFilter extends DateFilter {
    static final String NAME = "relativeDateFilter";
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
        this.from = notNull(from, "from");
        this.to = notNull(to, "to");
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
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
