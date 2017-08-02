/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;

/**
 * Thrown when specific S3 credentials, identified by warehouse ID, region and access key cannot be found
 */
public class WarehouseS3CredentialsNotFoundException extends GoodDataException {

    private final String warehouseS3CredentialsUri;

    public WarehouseS3CredentialsNotFoundException(String uri, GoodDataRestException cause) {
        super("Warehouse S3 credentials " + uri + " not found", cause);
        this.warehouseS3CredentialsUri = uri;
    }

    public String getWarehouseS3CredentialsUri() {
        return this.warehouseS3CredentialsUri;
    }
}
