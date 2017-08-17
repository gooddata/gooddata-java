/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.GoodDataException;

/**
 * Thrown when REST operation failed for S3 credentials
 */
public class WarehouseS3CredentialsException extends GoodDataException {

    private final String uri;

    public WarehouseS3CredentialsException(String uri, String message, Exception cause) {
        super(message, cause);
        this.uri = uri;
    }

    public WarehouseS3CredentialsException(final String uri, final String message) {
        this(uri, message, null);
    }

    public String getUri() {
        return uri;
    }
}
