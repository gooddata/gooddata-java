/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
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
