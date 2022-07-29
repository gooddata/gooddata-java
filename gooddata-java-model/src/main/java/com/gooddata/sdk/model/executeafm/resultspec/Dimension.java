/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.afm.Afm;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * Dimension content definition. Dimension contains one or more attributes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dimension {

    /**
     * Marker element to be used within itemIdentifiers marking the placement of measures
     */
    public static final String MEASURE_GROUP = "measureGroup";

    private final List<String> itemIdentifiers;
    private Set<TotalItem> totals;

    /**
     * Creates new instance
     * @param itemIdentifiers identifiers referencing attributes from {@link Afm} or {@link Dimension#MEASURE_GROUP}
     * @param totals set of totals
     */
    @JsonCreator
    public Dimension(
            @JsonProperty("itemIdentifiers") final List<String> itemIdentifiers,
            @JsonProperty("totals") final Set<TotalItem> totals) {
        this.itemIdentifiers = itemIdentifiers;
        this.totals = totals;
    }

    /**
     * Creates new instance
     * @param itemIdentifiers identifiers referencing attributes from {@link Afm} or {@link Dimension#MEASURE_GROUP}
     */
    public Dimension(final List<String> itemIdentifiers) {
        this.itemIdentifiers = itemIdentifiers;
    }

    /**
     * Creates new instance
     * @param itemIdentifiers identifiers referencing attributes from {@link Afm} or {@link Dimension#MEASURE_GROUP}
     */
    public Dimension(final String... itemIdentifiers) {
        this(asList(itemIdentifiers));
    }

    public List<String> getItemIdentifiers() {
        return itemIdentifiers;
    }

    public Set<TotalItem> getTotals() {
        return totals;
    }

    public void setTotals(final Set<TotalItem> totals) {
        this.totals = totals;
    }

    public Dimension addTotal(final TotalItem total) {
        if (totals == null) {
            setTotals(new HashSet<>());
        }
        totals.add(notNull(total, "total"));
        return this;
    }

    public Set<TotalItem> findTotals(final String attrIdentifier) {
        final Predicate<TotalItem> filter = t -> notNull(attrIdentifier, "attrIdentifier").equals(t.getAttributeIdentifier());

        if (totals == null) {
            return Collections.emptySet();
        } else {
            return totals.stream().filter(filter).collect(Collectors.toSet());
        }
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
