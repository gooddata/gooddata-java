/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GDDateDeserializer;
import com.gooddata.util.GDDateSerializer;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.LocalDate;

import static com.gooddata.util.Validate.notNull;

/**
 * Represents {@link DateFilter} specifying exact from and to dates.
 */
@JsonRootName(AbsoluteDateFilter.NAME)
public class AbsoluteDateFilter extends DateFilter {
    static final String NAME = "absoluteDateFilter";
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates new filter instance
     * @param dataSet qualifier of date dimension dataset
     * @param from date from
     * @param to date to
     */
    @JsonCreator
    public AbsoluteDateFilter(@JsonProperty("dataSet") final ObjQualifier dataSet,
                              @JsonProperty("from") @JsonDeserialize(using = GDDateDeserializer.class) final LocalDate from,
                              @JsonProperty("to") @JsonDeserialize(using = GDDateDeserializer.class) final LocalDate to) {
        super(dataSet);
        this.from = notNull(from, "from");
        this.to = notNull(to, "to");
    }

    /**
     * @return date from
     */
    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getFrom() {
        return from;
    }

    /**
     * @return date to
     */
    @JsonSerialize(using = GDDateSerializer.class)
    public LocalDate getTo() {
        return to;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new AbsoluteDateFilter(qualifier, from, to);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
