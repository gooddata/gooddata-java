/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum containing ETL lookup modes.
 */
public enum LookupMode {

    RECREATE;

    @JsonValue
    public String getName() {
        return name().toLowerCase();
    }
}
