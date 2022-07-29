/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Direction {

    ASC, DESC;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
