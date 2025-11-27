/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * Filter of attribute filtering by NOT IN expression
 */
@JsonRootName(NegativeAttributeFilter.NAME)
public class NegativeAttributeFilter extends AttributeFilter {

    static final String NAME = "negativeAttributeFilter";
    private static final long serialVersionUID = -6202625318104289333L;
    private final AttributeFilterElements notIn;

    /**
     * Creates new instance of given display form and not in list
     *
     * @param displayForm display form
     * @param notIn       list of not in elements
     * @deprecated for compatibility with version 2.x only, use {@link #NegativeAttributeFilter(ObjQualifier, AttributeFilterElements)} instead
     */
    @Deprecated
    public NegativeAttributeFilter(@JsonProperty("displayForm") final ObjQualifier displayForm,
                                   @JsonProperty("notIn") final List<String> notIn) {
        this(displayForm, new SimpleAttributeFilterElements(notIn));
    }

    /**
     * Creates new instance of given display form and not in list
     *
     * @param displayForm display form
     * @param notIn       not in elements (uris or values)
     */
    @JsonCreator
    public NegativeAttributeFilter(@JsonProperty("displayForm") final ObjQualifier displayForm,
                                   @JsonProperty("notIn") final AttributeFilterElements notIn) {
        super(displayForm);
        this.notIn = notIn;
    }

    /**
     * @deprecated for compatibility with version 2.x only, use {@link #NegativeAttributeFilter(ObjQualifier, AttributeFilterElements)} instead
     */
    @Deprecated
    public NegativeAttributeFilter(final ObjQualifier displayForm, final String... notIn) {
        this(displayForm, asList(notIn));
    }

    /**
     * @return not in elements
     */
    public AttributeFilterElements getNotIn() {
        return notIn;
    }

    /**
     * @return true if filter contains no elements, false otherwise
     */
    @JsonIgnore
    public boolean isAllSelected() {
        return notIn.getElements().isEmpty();
    }

    @Override
    public FilterItem withObjUriQualifier(final UriObjQualifier qualifier) {
        return new NegativeAttributeFilter(qualifier, notIn);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NegativeAttributeFilter that = (NegativeAttributeFilter) o;
        return super.equals(that) && Objects.equals(notIn, that.notIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notIn, super.hashCode());
    }

}
