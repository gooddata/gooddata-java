/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

import org.codehaus.jackson.JsonNode;

import static com.gooddata.Validate.notNull;

/**
 * Executed report
 */
class ExecResult {

    private final JsonNode jsonNode;

    ExecResult(JsonNode jsonNode) {
        this.jsonNode = notNull(jsonNode, "jsonNode");
    }

    JsonNode getJsonNode() {
        return jsonNode;
    }
}
