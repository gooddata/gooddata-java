/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum containing ETL lookup modes.
 */
enum LookupMode {

    RECREATE;

    @JsonValue
    public String getName() {
        return name().toLowerCase();
    }
}
