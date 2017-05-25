/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListSerializer;

import static com.gooddata.warehouse.WarehouseUsers.ROOT_NODE;

/**
 * Serializer of warehouse users object into JSON.
 */
class WarehouseUsersSerializer extends PageableListSerializer {

    public WarehouseUsersSerializer() {
        super(ROOT_NODE);
    }

}
