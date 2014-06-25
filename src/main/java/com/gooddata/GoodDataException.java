/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

/**
 * Signals that something unexpected happened in GoodData SDK.
 */
public class GoodDataException extends RuntimeException {

    /**
     * Construct a GoodDataException with the specified detail message.
     *
     * @param message the detail message
     */
    public GoodDataException(String message) {
        super(message);
    }

    /**
     * Construct a GoodDataException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public GoodDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
