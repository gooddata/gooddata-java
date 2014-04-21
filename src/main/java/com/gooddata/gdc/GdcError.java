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
public class GdcError extends ErrorStructure {
    private final String requestId;

    @JsonCreator
    public GdcError(@JsonProperty("message") String message, @JsonProperty("parameters") String[] parameters,
                    @JsonProperty("requestId") String requestId, @JsonProperty("component") String component,
                    @JsonProperty("errorClass") String errorClass) {
        super(errorClass, component, parameters, message);
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

}
