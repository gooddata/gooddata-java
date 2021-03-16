/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * {@link ResultDimension}'s header.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MeasureGroupHeader.class, name = MeasureGroupHeader.JSON_ROOT_NAME),
        @JsonSubTypes.Type(value = AttributeHeader.class, name = AttributeHeader.JSON_ROOT_NAME)
})
public interface Header {
}
