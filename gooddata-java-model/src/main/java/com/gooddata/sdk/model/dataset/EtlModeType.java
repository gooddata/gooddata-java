/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum containing ETL mode types.
 */
public enum EtlModeType {

    SLI, DLI, VOID;

    @JsonValue
    public String getName() {
        return name();
    }
}

