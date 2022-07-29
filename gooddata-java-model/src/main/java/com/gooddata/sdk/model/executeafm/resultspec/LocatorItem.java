/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Locator hold information about specific element in path of elements.
 * It can be attribute element, metric, etc.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AttributeLocatorItem.class, name = "attributeLocatorItem"),
        @JsonSubTypes.Type(value = MeasureLocatorItem.class, name = "measureLocatorItem"),
        @JsonSubTypes.Type(value = TotalLocatorItem.class, name = "totalLocatorItem")
})
public interface LocatorItem {
}
