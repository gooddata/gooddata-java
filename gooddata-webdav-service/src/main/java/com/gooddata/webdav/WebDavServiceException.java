/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

/**
 * Exception thrown by WebDAV service operations
 */
public class WebDavServiceException extends Exception {

    private final int statusCode;
    private final String requestId;

    public WebDavServiceException(String message) {
        super(message);
        this.statusCode = -1;
        this.requestId = null;
    }

    public WebDavServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.requestId = null;
    }

    public WebDavServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
        this.requestId = null;
    }

    public WebDavServiceException(String message, int statusCode, String requestId) {
        super(message);
        this.statusCode = statusCode;
        this.requestId = requestId;
    }

    public WebDavServiceException(String message, int statusCode, String requestId, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.requestId = requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRequestId() {
        return requestId;
    }
}