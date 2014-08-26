package com.gooddata.dataload.processes;

import com.gooddata.GoodDataException;

/**
 * Represents failure during process execution
 */
public class ProcessExecutionException extends GoodDataException {


    private ProcessExecutionDetail executionDetail;
    private String executionDetailUri;

    public ProcessExecutionException(String message) {
        super(message);
    }

    public ProcessExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessExecutionException(String message, ProcessExecutionDetail executionDetail) {
        super(message);
        this.executionDetail = executionDetail;
    }

    public ProcessExecutionException(String message, Throwable cause, String executionDetailUri) {
        super(message, cause);
        this.executionDetailUri = executionDetailUri;
    }

    public ProcessExecutionDetail getExecutionDetail() {
        return executionDetail;
    }

    public String getExecutionDetailUri() {
        return executionDetailUri;
    }
}
