/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListSerializer;

class WarehouseS3CredentialsListSerializer extends PageableListSerializer {

    WarehouseS3CredentialsListSerializer() {
        super(WarehouseS3CredentialsList.ROOT_NODE);
    }
}
