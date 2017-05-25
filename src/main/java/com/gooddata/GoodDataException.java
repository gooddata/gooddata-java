/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
