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

import static java.util.Arrays.asList;

/**
 * Filter of attribute filtering by NOT IN expression
 */
@JsonRootName(NegativeAttributeFilter.NAME)
public class NegativeAttributeFilter extends AttributeFilter {
    static final String NAME = "negativeAttributeFilter";
    private final List<String> notIn;

    /**
     * Creates new instance of given display form and not in list
     * @param displayForm display form
     * @param notIn list of not in elements
     */
    @JsonCreator
    public NegativeAttributeFilter(@JsonProperty("displayForm") final ObjQualifier displayForm,
                                   @JsonProperty("notIn") final List<String> notIn) {
        super(displayForm);
        this.notIn = notIn;
    }

    public NegativeAttributeFilter(final ObjQualifier displayForm, final String... notIn) {
        this(displayForm, asList(notIn));
    }

    /**
     * @return list of not in elements
     */
    public List<String> getNotIn() {
        return notIn;
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new NegativeAttributeFilter(qualifier, notIn);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
