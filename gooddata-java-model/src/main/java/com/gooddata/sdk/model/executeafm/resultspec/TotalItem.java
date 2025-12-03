/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.model.md.report.Total;

import java.util.Objects;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Total definition
 */
public class TotalItem {
    private final String measureIdentifier;
    private final String type;
    private final String attributeIdentifier;

    /**
     * Total definition
     *
     * @param measureIdentifier   measure on which is total defined
     * @param type                total type
     * @param attributeIdentifier internal attribute identifier in AFM defining total placement
     */
    @JsonCreator
    public TotalItem(
            @JsonProperty("measureIdentifier") final String measureIdentifier, @JsonProperty("type") final String type,
            @JsonProperty("attributeIdentifier") final String attributeIdentifier) {
        this.measureIdentifier = notEmpty(measureIdentifier, "measureIdentifier");
        this.type = type;
        this.attributeIdentifier = notEmpty(attributeIdentifier, "attributeIdentifier");
    }

    public TotalItem(final String measureIdentifier, final Total total, final String attributeIdentifier) {
        this(measureIdentifier, notNull(total, "total").toString(), attributeIdentifier);
    }

    /**
     * total type
     *
     * @return total type
     */
    public String getType() {
        return type;
    }

    /**
     * internal measure identifier in AFM, on which is total defined
     *
     * @return measure
     */
    public String getMeasureIdentifier() {
        return measureIdentifier;
    }

    /**
     * internal attribute identifier in AFM defining total placement
     *
     * @return identifier (never null)
     */
    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TotalItem totalItem = (TotalItem) o;
        return Objects.equals(measureIdentifier, totalItem.measureIdentifier) &&
                Objects.equals(type, totalItem.type) &&
                Objects.equals(attributeIdentifier, totalItem.attributeIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(measureIdentifier, type, attributeIdentifier);
    }
}
