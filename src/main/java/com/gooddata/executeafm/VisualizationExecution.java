/*
 * Copyright (C) 2004-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.executeafm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.executeafm.afm.CompatibilityFilter;
import com.gooddata.executeafm.resultspec.ResultSpec;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Represents structure for triggering execution with reference to visualization object.
 * Contains additional filters which should be merged with original ones defined in viz. object.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("visualizationExecution")
public class VisualizationExecution {

    private final String reference;
    private List<CompatibilityFilter> filters;
    private ResultSpec resultSpec;

    /**
     * Constructor.
     * Creates execution of visualization object without any additional filters or result specification.
     *
     * @param reference reference uri to visualization object metadata
     */
    public VisualizationExecution(final String reference) {
        this(reference, null, null);
    }

    /**
     * @param reference reference uri to visualization object metadata
     * @param filters additional filters which should be merged
     * @param resultSpec result specification of executed viz. object
     */
    @JsonCreator
    VisualizationExecution(@JsonProperty("reference") final String reference,
                           @JsonProperty("filters") final List<CompatibilityFilter> filters,
                           @JsonProperty("resultSpec") final ResultSpec resultSpec) {
        this.reference = reference;
        this.resultSpec = resultSpec;
        this.filters = filters;
    }

    /**
     * @return reference uri to visualization object metadata
     */
    public String getReference() {
        return reference;
    }

    public List<CompatibilityFilter> getFilters() {
        return filters;
    }

    /**
     * Sets the result specification and returns this instance
     *
     * @param resultSpec result specification of executed viz. object
     * @return updated execution
     */
    public VisualizationExecution setResultSpec(final ResultSpec resultSpec) {
        this.resultSpec = resultSpec;
        return this;
    }

    /**
     * @return result specification of executed viz. object
     */
    public ResultSpec getResultSpec() {
        return resultSpec;
    }

    /**
     * Sets additional filters to this execution.
     *
     * @param filters additional filters
     * @return updated execution
     */
    public VisualizationExecution setFilters(final List<CompatibilityFilter> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VisualizationExecution that = (VisualizationExecution) o;
        return Objects.equals(reference, that.reference) &&
                Objects.equals(filters, that.filters) &&
                Objects.equals(resultSpec, that.resultSpec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, filters, resultSpec);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
