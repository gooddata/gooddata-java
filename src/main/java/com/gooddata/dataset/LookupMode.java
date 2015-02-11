package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonValue;

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
