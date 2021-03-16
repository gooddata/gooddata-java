/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Parent interface for filter implementation inside {@link DashboardFilterContext}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DashboardDateFilter.class, name = DashboardDateFilter.JSON_ROOT_NAME),
        @JsonSubTypes.Type(value = DashboardAttributeFilter.class, name = DashboardAttributeFilter.JSON_ROOT_NAME)
})
public interface DashboardFilter extends Serializable {
}
