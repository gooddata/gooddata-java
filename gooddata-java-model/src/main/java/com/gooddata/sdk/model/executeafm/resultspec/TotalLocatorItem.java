/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

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
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
