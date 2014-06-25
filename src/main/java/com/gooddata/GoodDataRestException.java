/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.gdc.GdcError;

/**
 * Signals client or server error during communication with GoodData REST API.
 */
public class GoodDataRestException extends GoodDataException {

    private final int statusCode;

    private final String requestId;

    private final String component;

    private final String errorClass;

    /**
     * Construct a GoodDataRestException with specified details.
     *
     * @param statusCode the HTTP status code of the response
     * @param requestId the GoodData request ID (from header)
     * @param message the detail message
     * @param component the GoodData component where error occurred
     * @param errorClass the class of the error
     */
    public GoodDataRestException(int statusCode, String requestId, String message, String component,
                                 String errorClass) {
        super(statusCode + ": [requestId=" + requestId + "] " + message);
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.component = component;
        this.errorClass = errorClass;
    }

    /**
     * Construct a GoodDataRestException with specified details.
     *
     * @param statusCode the HTTP status code of the response
     * @param requestId the GoodData request ID (from header)
     * @param statusText the HTTP status text of the response
     * @param error the GoodData REST API error structure
     */
    public GoodDataRestException(int statusCode, String requestId, String statusText, GdcError error) {
        this(statusCode,
                error != null && error.getRequestId() != null ? error.getRequestId() : requestId,
                error != null && error.getMessage() != null ? error.getFormattedMessage() : statusText,
                error != null ? error.getComponent() : null,
                error != null ? error.getErrorClass() : null);
    }

    public int getStatusCode() {
        return statusCode;
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
