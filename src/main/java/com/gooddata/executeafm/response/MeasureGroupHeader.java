/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Header of a measure group.
 */
@JsonRootName(MeasureGroupHeader.NAME)
public class MeasureGroupHeader implements Header {

    static final String NAME = "measureGroupHeader";

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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MeasureGroupHeader that = (MeasureGroupHeader) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
