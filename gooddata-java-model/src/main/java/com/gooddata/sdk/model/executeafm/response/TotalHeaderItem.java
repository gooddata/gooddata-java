/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.md.report.Total;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notNull;

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
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
