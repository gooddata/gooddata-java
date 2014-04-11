package com.gooddata.task;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * TODO
 */
public class PullTaskStatus {

    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private final String status;

    @JsonCreator
    public PullTaskStatus(@JsonProperty("taskStatus") String status) {
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
