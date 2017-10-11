/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;

/**
 * {@link ResultDimension}'s header.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeasureGroupHeader.class, name = MeasureGroupHeader.NAME),
        @JsonSubTypes.Type(value = AttributeHeader.class, name = AttributeHeader.NAME)
})
public abstract class Header {
    private List<TotalHeaderItem> totalItems;

    /**
     * Creates new header
     */
    protected Header() {
    }

    /**
     * Creates new header of given total items
     * @param totalItems total header items
     */
    protected Header(final List<TotalHeaderItem> totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Totals' headers belonging to the same level as this header.
     * @return lists of totals' header
     */
    public List<TotalHeaderItem> getTotalItems() {
        return totalItems;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
