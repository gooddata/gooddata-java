/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListSerializer;

class WarehouseS3CredentialsListSerializer extends PageableListSerializer {

    WarehouseS3CredentialsListSerializer() {
        super(WarehouseS3CredentialsList.ROOT_NODE);
    }
}
