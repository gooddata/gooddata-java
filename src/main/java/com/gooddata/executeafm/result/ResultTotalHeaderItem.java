/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.md.report.Total;

import static com.gooddata.executeafm.result.ResultTotalHeaderItem.NAME;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Header item for total.
 */
@JsonRootName(NAME)
public class ResultTotalHeaderItem extends ResultHeaderItem {

    static final String NAME = "totalHeaderItem";

    private final String type;

    /**
     * Creates new instance of given total type, type is used for the name as well
     * @param type total type
     */
    public ResultTotalHeaderItem(final String type) {
        this(type, type);
    }

    /**
     * Creates new instance of given total type, type is used for the name as well
     * @param type total type
     */
    public ResultTotalHeaderItem(final Total type) {
        this(notNull(type, "type").toString());
    }

    /**
     * Creates new instance of given header name and total type
     * @param name header name
     * @param type total type
     */
    @JsonCreator
    public ResultTotalHeaderItem(@JsonProperty("name") final String name, @JsonProperty("type") final String type) {
        super(name);
        this.type = notEmpty(type, "type");
    }

    /**
     * Creates new instance of given header name and total type
     * @param name header name
     * @param type total type
     */
    public ResultTotalHeaderItem(final String name, final Total type) {
        super(name);
        this.type = notNull(type, "type").toString();
    }

    /**
     * @return type of total
     */
    public String getType() {
        return type;
    }
}
