/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Objects;

import static com.gooddata.executeafm.result.ResultMeasureHeaderItem.NAME;

/**
 * Header item for measure
 */
@JsonRootName(NAME)
public class ResultMeasureHeaderItem extends ResultHeaderItem {

    static final String NAME = "measureHeaderItem";

    private final int order;

    /**
     * Creates new instance of given header name and order
     * @param name header name
     * @param order measure order within measureGroup
     */
    @JsonCreator
    public ResultMeasureHeaderItem(@JsonProperty("name") final String name, @JsonProperty("order") final int order) {
        super(name);
        this.order = order;
    }

    /**
     * @return measure order within measureGroup
     */
    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final ResultMeasureHeaderItem that = (ResultMeasureHeaderItem) o;
        return order == that.order;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), order);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
