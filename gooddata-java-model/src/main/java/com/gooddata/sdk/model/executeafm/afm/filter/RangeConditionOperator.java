/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents all the possible operators of {@link RangeCondition}.
 */
public enum RangeConditionOperator {
    BETWEEN,
    NOT_BETWEEN;

    @JsonValue
    @Override
    public String toString() {
        return name();
    }
}
