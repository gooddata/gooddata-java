/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * Metadata bulk get request body representation.
 * Serialization only.
 */
@JsonTypeName("get")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class BulkGetUris {

    private final Collection<String> items;

    public BulkGetUris(Collection<String> items) {
        notNull(items, "items");
        this.items = items;
    }

    @JsonProperty("items")
    public Collection<String> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkGetUris that = (BulkGetUris) o;

        if (!items.equals(that.items)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }
}
