/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.copyOf;

/**
 * Error structure (for embedding).
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ErrorStructureDeserializer.class)
public class ErrorStructure {
    protected final String message;
    protected final Object[] parameters;
    protected final String component;
    protected final String errorClass;
    protected final String errorCode;
    protected final String errorId;
    protected final String trace;
    protected final String requestId;

    @JsonCreator
    protected ErrorStructure(@JsonProperty("errorClass") String errorClass, @JsonProperty("component") String component,
                             @JsonProperty("parameters") Object[] parameters, @JsonProperty("message") String message,
                             @JsonProperty("errorCode") String errorCode, @JsonProperty("errorId") String errorId,
                             @JsonProperty("trace") String trace, @JsonProperty("requestId") String requestId) {
        this.errorClass = errorClass;
        this.component = component;
        this.parameters = parameters;
        this.message = notNull(message, "message");
        this.errorCode = errorCode;
        this.errorId = errorId;
        this.trace = trace;
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getParameters() {
        return parameters == null ? null : copyOf(parameters, parameters.length);
    }

    public String getComponent() {
        return component;
    }

    public String getErrorClass() {
        return errorClass;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getTrace() {
        return trace;
    }

    public String getRequestId() {
        return requestId;
    }

    @JsonIgnore
    public String getFormattedMessage() {
        return message == null ? null : String.format(message, parameters);
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
