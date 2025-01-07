/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.afm.LocallyIdentifiable;
import com.gooddata.sdk.model.executeafm.resultspec.TotalItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents bucket within {@link VisualizationObject}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bucket implements Serializable, LocallyIdentifiable {

    private static final long serialVersionUID = -7718720886547680021L;
    private final String localIdentifier;
    private final List<BucketItem> items;
    private final List<TotalItem> totals;

    /**
     * Creates new instance of bucket without totals
     *
     * @param localIdentifier local identifier of bucket
     * @param items           list of {@link BucketItem}s for this bucket
     */
    public Bucket(@JsonProperty("localIdentifier") final String localIdentifier,
            @JsonProperty("items") final List<BucketItem> items) {
        this(localIdentifier, items, null);
    }

    /**
     * Creates new instance of bucket
     *
     * @param localIdentifier local identifier of bucket
     * @param items           list of {@link BucketItem}s for this bucket
     * @param totals          list of {@link TotalItem}s for this bucket
     */
    @JsonCreator
    public Bucket(@JsonProperty("localIdentifier") final String localIdentifier,
            @JsonProperty("items") final List<BucketItem> items,
            @JsonProperty("totals") List<TotalItem> totals) {
        this.localIdentifier = localIdentifier;
        this.items = items;
        this.totals = totals;
    }

    /**
     * @return local identifier
     */
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * @return list of {@link BucketItem}s
     */
    public List<BucketItem> getItems() {
        return items;
    }

    /**
     * @return list of defined {@link TotalItem}s
     */
    public List<TotalItem> getTotals() {
        return totals;
    }

    @JsonIgnore
    VisualizationAttribute getOnlyAttribute() {
        if (getItems() != null && getItems().size() == 1) {
            final BucketItem item = getItems().iterator().next();
            if (item instanceof VisualizationAttribute) {
                return (VisualizationAttribute) item;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Bucket bucket = (Bucket) o;
        return Objects.equals(localIdentifier, bucket.localIdentifier)
                && Objects.equals(items, bucket.items)
                && Objects.equals(totals, bucket.totals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localIdentifier, items, totals);
    }
}
