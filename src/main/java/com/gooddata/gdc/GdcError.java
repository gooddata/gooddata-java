/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * GoodData REST API error structure
 */
@JsonTypeName("error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GdcError {
    private final String message;
    private final String[] parameters;
    private final String requestId;
    private final String component;
    private final String errorClass;

    @JsonCreator
    public GdcError(@JsonProperty("message") String message, @JsonProperty("parameters") String[] parameters,
                    @JsonProperty("requestId") String requestId, @JsonProperty("component") String component,
                    @JsonProperty("errorClass") String errorClass) {
        this.message = message;
        this.parameters = parameters;
        this.requestId = requestId;
        this.component = component;
        this.errorClass = errorClass;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParameters() {
        return parameters;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getComponent() {
        return component;
    }

    public String getErrorClass() {
        return errorClass;
    }
}
