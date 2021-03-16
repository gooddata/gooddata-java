/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents aggregation used when putting together all related
 * values belonging to attribute element when we are sorting by this
 * attribute. You can find more details in {@link AttributeSortItem}.
 */
public enum AttributeSortAggregation {
    SUM;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
