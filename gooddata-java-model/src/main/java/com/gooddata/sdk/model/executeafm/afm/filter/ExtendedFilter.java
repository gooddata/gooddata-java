/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FilterItem.class),
        @JsonSubTypes.Type(value = MeasureValueFilter.class, name = MeasureValueFilter.NAME),
        @JsonSubTypes.Type(value = RankingFilter.class, name = RankingFilter.NAME),
})
public interface ExtendedFilter {
}
