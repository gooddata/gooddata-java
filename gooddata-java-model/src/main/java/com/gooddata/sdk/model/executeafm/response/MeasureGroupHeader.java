/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

/**
 * Header of a measure group.
 */
@JsonRootName(MeasureGroupHeader.JSON_ROOT_NAME)
public class MeasureGroupHeader implements Header {

    static final String JSON_ROOT_NAME = "measureGroupHeader";

    private final List<MeasureHeaderItem> items;

    /**
     * Creates new header
     * @param items header items
     */
    @JsonCreator
    public MeasureGroupHeader(@JsonProperty("items") final List<MeasureHeaderItem> items) {
        this.items = items;
    }

    /**
     * Header items for particular measures
     * @return header items
     */
    public List<MeasureHeaderItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
