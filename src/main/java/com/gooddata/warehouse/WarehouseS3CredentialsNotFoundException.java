/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.GoodDataRestException;

/**
 * Thrown when specific S3 credentials, identified by warehouse ID, region and access key cannot be found
 */
public class WarehouseS3CredentialsNotFoundException extends WarehouseS3CredentialsException {
    public WarehouseS3CredentialsNotFoundException(String uri, GoodDataRestException cause) {
        super(uri, "Warehouse S3 credentials " + uri + " not found", cause);
    }
}
