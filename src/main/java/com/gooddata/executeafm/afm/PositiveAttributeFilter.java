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

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * Filter of attribute filtering by IN expression
 */
@JsonRootName(PositiveAttributeFilter.NAME)
public class PositiveAttributeFilter extends AttributeFilter {

    private static final long serialVersionUID = 1934771670274345290L;
    static final String NAME = "positiveAttributeFilter";

    private final List<String> in;

    /**
     * Creates new instance of given display form and in list
     * @param displayForm display form
     * @param in list of in elements
     */
    @JsonCreator
    public PositiveAttributeFilter(@JsonProperty("displayForm") final ObjQualifier displayForm,
                                   @JsonProperty("in") final List<String> in) {
        super(displayForm);
        this.in = in;
    }

    public PositiveAttributeFilter(final ObjQualifier displayForm, final String... in) {
        this(displayForm, asList(in));
    }

    /**
     * @return list of in elements
     */
    public List<String> getIn() {
        return in;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new PositiveAttributeFilter(qualifier, in);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositiveAttributeFilter that = (PositiveAttributeFilter) o;
        return super.equals(that) && Objects.equals(in, that.in);
    }

    @Override
    public int hashCode() {
        return Objects.hash(in, super.hashCode());
    }
}
