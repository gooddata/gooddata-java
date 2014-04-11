/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

/**
 */
public class GoodDataException extends RuntimeException {

    public GoodDataException(String message) {
        super(message);
    }

    public GoodDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
