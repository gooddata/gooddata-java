/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gooddata.executeafm.afm.SimpleMeasureDefinition.NAME;
import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * Definition of simple measure
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(NAME)
public class SimpleMeasureDefinition implements MeasureDefinition, Serializable {

    private static final long serialVersionUID = -385490772711914776L;
    static final String NAME = "measure";

    private final ObjQualifier item;
    private String aggregation;
    private Boolean computeRatio;
    private List<FilterItem> filters;

    public SimpleMeasureDefinition(final ObjQualifier item) {
        this.item = item;
    }

    /**
     * Creates new definition
     * @param item item which is measured, can be attribute, fact or another measure
     * @param aggregation additional aggregation applied
     * @param computeRatio whether should be shown as ratio
     * @param filters additional filters applied
     */
    @JsonCreator
    public SimpleMeasureDefinition(@JsonProperty("item") final ObjQualifier item,
                                   @JsonProperty("aggregation") final String aggregation,
                                   @JsonProperty("computeRatio") final Boolean computeRatio,
                                   @JsonProperty("filters") final List<FilterItem> filters) {
        this.item = item;
        this.aggregation = aggregation;
        this.computeRatio = computeRatio;
        this.filters = filters;
    }

    /**
     * Creates new definition
     * @param item item which is measured, can be attribute, fact or another measure
     * @param aggregation additional aggregation applied
     * @param computeRatio whether should be shown as ratio
     * @param filters additional filters applied
     */
    public SimpleMeasureDefinition(final ObjQualifier item, final Aggregation aggregation, final Boolean computeRatio,
                                   final List<FilterItem> filters) {
        this(item, notNull(aggregation, "aggregation").toString(), computeRatio, filters);
    }

    /**
     * Creates new definition
     * @param item item which is measured, can be attribute, fact or another measure
     * @param aggregation additional aggregation applied
     * @param computeRatio whether should be shown as ratio
     * @param filters additional filters applied
     */
    public SimpleMeasureDefinition(final ObjQualifier item, final Aggregation aggregation, final Boolean computeRatio,
                                   final FilterItem... filters) {
        this(item, aggregation, computeRatio, asList(filters));
    }

    @Override
    public MeasureDefinition withObjUriQualifier(final UriObjQualifier qualifier) {
        return new SimpleMeasureDefinition(qualifier, aggregation, computeRatio, filters);
    }

    @Override
    public boolean isAdHoc() {
        return hasAggregation() || hasComputeRatio() || hasFilters();
    }

    @Override
    public String getUri() {
        return getItem().getUri();
    }

    @Override
    public ObjQualifier getObjQualifier() {
        return getItem();
    }

    /**
     * @return measured item, can be attribute, fact or another measure
     */
    public ObjQualifier getItem() {
        return item;
    }

    /**
     * @return additional aggregation applied
     */
    public String getAggregation() {
        return aggregation;
    }

    /**
     * Set additional aggregation applied
     * @param aggregation additional aggregation applied
     */
    public void setAggregation(final String aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * Set additional aggregation applied
     * @param aggregation additional aggregation applied
     */
    public void setAggregation(final Aggregation aggregation) {
        setAggregation(notNull(aggregation, "aggregation").toString());
    }

    /**
     * @return true when should be shown as ratio, false otherwise
     */
    public Boolean getComputeRatio() {
        return computeRatio;
    }

    /**
     * Set whether should be shown as ratio
     * @param computeRatio whether should be shown as ratio
     */
    public void setComputeRatio(final Boolean computeRatio) {
        this.computeRatio = computeRatio;
    }

    /**
     * @return additional filters applied
     */
    public List<FilterItem> getFilters() {
        return filters;
    }

    /**
     * Set additional filters applied
     * @param filters additional filters applied
     */
    public void setFilters(final List<FilterItem> filters) {
        this.filters = filters;
    }

    /**
     * Apply additional filter
     * @param filter filter to be applied
     */
    public void addFilter(final FilterItem filter) {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        filters.add(notNull(filter, "filter"));
    }

    /**
     * @return true when filters are set, false otherwise
     */
    public boolean hasFilters() {
        return filters != null && !filters.isEmpty();
    }

    /**
     * @return true when computeRatio is set, false otherwise
     */
    public boolean hasComputeRatio() {
        return computeRatio != null && computeRatio;
    }

    /**
     * @return true when additional aggregation is set, false otherwise
     */
    public boolean hasAggregation() {
        return aggregation != null;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

