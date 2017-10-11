/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * Define metric sort
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("measureSortItem")
public class MeasureSortItem implements SortItem {
    private final String direction;
    private final List<LocatorItem> locators;

    @JsonCreator
    public MeasureSortItem(
            @JsonProperty("direction") final String direction,
            @JsonProperty("locators") final List<LocatorItem> locators) {
        this.direction = direction;
        this.locators = locators;
    }

    public MeasureSortItem(final Direction direction, final List<LocatorItem> locators) {
        this(notNull(direction, "direction").toString(), locators);
    }

    public MeasureSortItem(final Direction direction, final LocatorItem... locators) {
        this(direction, asList(locators));
    }

    public String getDirection() {
        return direction;
    }

    public List<LocatorItem> getLocators() {
        return locators;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
