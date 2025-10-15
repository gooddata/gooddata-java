/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.List;

/**
 * Attribute filter located on analytical dashboard.
 * Is not standalone metadata object - always must be part of {@link DashboardFilterContext}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(DashboardAttributeFilter.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardAttributeFilter implements DashboardFilter {

    static final String NAME = "attributeFilter";

    private final String displayForm;
    private final boolean negativeSelection;
    private final List<String> attributeElements;

    /**
     * Constructor.
     *
     * @param displayForm display form of an attribute where this filter is applied
     * @param negativeSelection if the negative selection of filter elements is applied
     * @param attributeElementUris list of attribute element URIs applied in filter
     */
    @JsonCreator
    public DashboardAttributeFilter(
            @JsonProperty("displayForm") final String displayForm,
            @JsonProperty("negativeSelection") final boolean negativeSelection,
            @JsonProperty("attributeElements") final List<String> attributeElementUris) {
        this.displayForm = notEmpty(displayForm, "displayForm");
        this.negativeSelection = negativeSelection;
        this.attributeElements = notEmpty(attributeElementUris, "attributeElementUris");
    }

    /**
     * @return display form of an attribute where this filter is applied
     */
    public String getDisplayForm() {
        return displayForm;
    }

    /**
     * @return if this filter is negative or positive selection of attribute elements contained in {@link #getAttributeElementUris()}
     */
    public boolean isNegativeSelection() {
        return negativeSelection;
    }

    /**
     * @return list of attribute element URI strings which should be included or excluded in filter
     *
     * @see #isNegativeSelection()
     */
    @JsonIgnore
    public List<String> getAttributeElementUris() {
        return Collections.unmodifiableList(getAttributeElements());
    }

    @JsonProperty
    private List<String> getAttributeElements() {
        return attributeElements;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toString(this);
    }
}

