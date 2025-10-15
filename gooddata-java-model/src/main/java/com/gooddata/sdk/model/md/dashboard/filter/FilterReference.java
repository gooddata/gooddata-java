/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * Parent interface for ignored filter references inside {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DateFilterReference.class, name = DateFilterReference.NAME),
        @JsonSubTypes.Type(value = AttributeFilterReference.class, name = AttributeFilterReference.NAME)
})
public interface FilterReference extends Serializable {
}

