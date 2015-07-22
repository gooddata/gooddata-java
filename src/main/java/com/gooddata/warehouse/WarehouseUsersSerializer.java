/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
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
