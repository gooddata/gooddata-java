/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Holds metric position
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonTypeName("measureLocatorItem")
public class MeasureLocatorItem implements LocatorItem {
    private final String measureIdentifier;

    @JsonCreator
    public MeasureLocatorItem(
            @JsonProperty("measureIdentifier") final String measureIdentifier) {
        this.measureIdentifier = measureIdentifier;
    }

    public String getMeasureIdentifier() {
        return measureIdentifier;
    }

    @Override
    public String toString() {
        return measureIdentifier;
    }
}
