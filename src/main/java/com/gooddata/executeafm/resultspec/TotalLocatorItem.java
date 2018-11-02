/*
 * Copyright (C) 2004-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Objects;

/**
 * Holds total type and attribute to which this total corresponds.
 * This enables sorting measure by specific totals.
 */
public class TotalLocatorItem implements LocatorItem {

    private final String attributeIdentifier;
    private final String totalType;

    @JsonCreator
    public TotalLocatorItem(
            @JsonProperty("attributeIdentifier") String attributeIdentifier,
            @JsonProperty("totalType") String totalType) {
        this.attributeIdentifier = attributeIdentifier;
        this.totalType = totalType;
    }

    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    public String getTotalType() {
        return totalType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TotalLocatorItem that = (TotalLocatorItem) o;
        return Objects.equals(attributeIdentifier, that.attributeIdentifier) &&
                Objects.equals(totalType, that.totalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeIdentifier, totalType);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
