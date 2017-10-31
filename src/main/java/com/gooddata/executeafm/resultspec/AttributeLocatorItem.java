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

/**
 * Holds attribute including it's specific element
 */
@JsonTypeInfo(include = As.WRAPPER_OBJECT, use = Id.NAME)
@JsonTypeName("attributeLocatorItem")
public class AttributeLocatorItem implements LocatorItem {
    private final String attributeIdentifier;
    private final String element;

    @JsonCreator
    public AttributeLocatorItem(
            @JsonProperty("attributeIdentifier") final String attributeIdentifier,
            @JsonProperty("element") final String element) {
        this.attributeIdentifier = attributeIdentifier;
        this.element = element;
    }

    public String getAttributeIdentifier() {
        return attributeIdentifier;
    }

    public String getElement() {
        return element;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
