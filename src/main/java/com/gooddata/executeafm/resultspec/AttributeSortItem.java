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

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import static com.gooddata.util.Validate.notNull;

/**
 * Define sort by specific attribute
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonTypeName("attributeSortItem")
public class AttributeSortItem implements SortItem {

    private final String direction;

    private final String attributeIdentifier;

    @JsonCreator
    public AttributeSortItem(@JsonProperty("direction") final String direction,
                             @JsonProperty("attributeIdentifier") final String attributeIdentifier) {
        this.attributeIdentifier = attributeIdentifier;
        this.direction = direction;
    }

    public AttributeSortItem(final Direction direction, final String attributeIdentifier) {
        this(notNull(direction, "direction").toString(), attributeIdentifier);
    }

    public String getDirection() {
        return direction;
    }

    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
