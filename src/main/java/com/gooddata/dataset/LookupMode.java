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
