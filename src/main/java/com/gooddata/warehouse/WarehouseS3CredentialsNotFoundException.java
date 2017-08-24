/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
