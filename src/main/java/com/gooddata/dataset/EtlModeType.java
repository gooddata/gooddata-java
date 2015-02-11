package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonValue;

/**
 * Enum containing ETL mode types.
 */
enum EtlModeType {

    SLI, DLI, VOID;

    @JsonValue
    public String getName() {
        return name();
    }
}
