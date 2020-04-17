/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.common.collections.PageSerializer;

/**
 * Serializer of warehouse users object into JSON.
 */
class WarehouseUsersSerializer extends PageSerializer {

    public WarehouseUsersSerializer() {
        super(WarehouseUsers.ROOT_NODE);
    }

}
