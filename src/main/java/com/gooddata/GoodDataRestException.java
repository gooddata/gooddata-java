/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.gdc.GdcError;

/**
 * A REST API exception
 */
public class GoodDataRestException extends GoodDataException {

    private final int statusCode;

    private final String requestId;

    private final String component;

    private final String errorClass;

    public GoodDataRestException(int statusCode, String requestId, String message, String component, String errorClass) {
        super(statusCode + ": [requestId=" + requestId + "] " + message);
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.component = component;
        this.errorClass = errorClass;
    }

    public GoodDataRestException (int statusCode, String requestId, String statusText, GdcError error) {
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
