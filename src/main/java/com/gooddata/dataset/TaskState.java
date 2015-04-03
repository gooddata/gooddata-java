/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * ETL Pull task status (for internal use).
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("taskState")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskState {

    private static final String OK = "OK";
    private static final String ERROR = "ERROR";

    private final String message;

    private final String status;

    @JsonCreator
    public TaskState(@JsonProperty("status") final String status, @JsonProperty("msg") final String message) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return OK.equals(status);
    }

    public boolean isFinished() {
        return OK.equals(status) || ERROR.equals(status);
    }
}
