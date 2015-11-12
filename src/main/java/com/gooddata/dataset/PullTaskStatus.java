/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ETL Pull task status (for internal use).
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PullTaskStatus {

    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private final String status;

    @JsonCreator
    private PullTaskStatus(@JsonProperty("taskStatus") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return OK.equals(status);
    }

    public boolean isFinished() {
        return OK.equals(status) || ERROR.equals(status) || WARNING.equals(status);
    }
}
