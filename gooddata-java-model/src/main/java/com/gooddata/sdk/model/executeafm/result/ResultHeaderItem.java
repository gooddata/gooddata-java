/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represent header items available in {@link ExecutionResult}
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttributeHeaderItem.class, name = AttributeHeaderItem.NAME),
        @JsonSubTypes.Type(value = ResultMeasureHeaderItem.class, name = ResultMeasureHeaderItem.NAME),
        @JsonSubTypes.Type(value = ResultTotalHeaderItem.class, name = ResultTotalHeaderItem.NAME)
})
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public abstract class ResultHeaderItem {

    private final String name;

    protected ResultHeaderItem(final String name) {
        this.name = notNull(name, "name");
    }

    /**
     * @return header item name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
