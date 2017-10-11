/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;

/**
 * Attributes Filters and Measures in so called object form (could have MAQL form in future)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectAfm {

    private List<AttributeItem> attributes;
    private List<CompatibilityFilter> filters;
    private List<MeasureItem> measures;
    private List<NativeTotalItem> nativeTotals;

    @JsonCreator
    public ObjectAfm(@JsonProperty("attributes") final List<AttributeItem> attributes,
                     @JsonProperty("filters") final List<CompatibilityFilter> filters,
                     @JsonProperty("measures") final List<MeasureItem> measures,
                     @JsonProperty("nativeTotals") final List<NativeTotalItem> nativeTotals) {
        this.attributes = attributes;
        this.filters = filters;
        this.measures = measures;
        this.nativeTotals = nativeTotals;
    }

    public ObjectAfm() {
    }

    /**
     * Find {@link AttributeItem} within attributes by given localIdentifier
     * @param localIdentifier identifier used for search
     * @return found attribute or throws exception
     */
    @JsonIgnore
    public AttributeItem getAttribute(final String localIdentifier) {
        return getIdentifiable(attributes, localIdentifier);
    }

    /**
     * Find {@link MeasureItem} within measures by given localIdentifier
     * @param localIdentifier identifier used for search
     * @return found measure or throws exception
     */
    @JsonIgnore
    public MeasureItem getMeasure(final String localIdentifier) {
        return getIdentifiable(measures, localIdentifier);
    }

    public List<AttributeItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(final List<AttributeItem> attributes) {
        this.attributes = attributes;
    }

    public ObjectAfm addAttribute(final AttributeItem attribute) {
        if (attributes == null) {
            setAttributes(new ArrayList<>());
        }
        attributes.add(notNull(attribute, "attribute"));
        return this;
    }

    public List<CompatibilityFilter> getFilters() {
        return filters;
    }

    public void setFilters(final List<CompatibilityFilter> filters) {
        this.filters = filters;
    }

    public ObjectAfm addFilter(final CompatibilityFilter filter) {
        if (filters == null) {
            setFilters(new ArrayList<>());
        }
        filters.add(notNull(filter, "filter"));
        return this;
    }

    public List<MeasureItem> getMeasures() {
        return measures;
    }

    public void setMeasures(final List<MeasureItem> measures) {
        this.measures = measures;
    }

    public ObjectAfm addMeasure(final MeasureItem measure) {
        if (measures == null) {
            setMeasures(new ArrayList<>());
        }
        measures.add(notNull(measure, "measure"));
        return this;
    }

    public List<NativeTotalItem> getNativeTotals() {
        return nativeTotals;
    }

    public void setNativeTotals(final List<NativeTotalItem> nativeTotals) {
        this.nativeTotals = nativeTotals;
    }

    public ObjectAfm addNativeTotal(final NativeTotalItem total) {
        if (nativeTotals == null) {
            setNativeTotals(new ArrayList<>());
        }
        nativeTotals.add(notNull(total, "total"));
        return this;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    private static <T extends LocallyIdentifiable> T getIdentifiable(final List<T> toSearch, final String localIdentifier) {
        return Optional.ofNullable(toSearch)
                .flatMap(a -> a.stream().filter(i -> Objects.equals(localIdentifier, i.getLocalIdentifier())).findFirst())
                .orElseThrow(() -> new IllegalArgumentException(format("Item of localIdentifier=%s not found", localIdentifier)));
    }

}
