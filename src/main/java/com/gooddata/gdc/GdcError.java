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
 * GoodData REST API error structure.
 * Deserialization only.
 */
@JsonTypeName("error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GdcError extends ErrorStructure {

    @JsonCreator
    private GdcError(@JsonProperty("errorClass") String errorClass, @JsonProperty("component") String component,
                     @JsonProperty("parameters") String[] parameters, @JsonProperty("message") String message,
                     @JsonProperty("errorCode") String errorCode, @JsonProperty("errorId") String errorId,
                     @JsonProperty("trace") String trace, @JsonProperty("requestId") String requestId) {
        super(errorClass, component, parameters, message, errorCode, errorId, trace, requestId);
    }

}
