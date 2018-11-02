/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.md.report.Total;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Objects;

import static com.gooddata.util.Validate.notNull;

/**
 * Header of total
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("totalHeaderItem")
public class TotalHeaderItem {

    private final String name;

    /**
     * Creates new header
     * @param name total name
     */
    @JsonCreator
    public TotalHeaderItem(@JsonProperty("name") final String name) {
        this.name = name;
    }

    /**
     * Creates new header
     * @param total total value
     */
    public TotalHeaderItem(final Total total) {
        this(notNull(total, "total").toString());
    }

    /**
     * @return name of total
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TotalHeaderItem that = (TotalHeaderItem) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
