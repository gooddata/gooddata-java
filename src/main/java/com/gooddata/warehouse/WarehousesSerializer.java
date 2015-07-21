/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListSerializer;

import static com.gooddata.warehouse.Warehouses.ROOT_NODE;

/**
 * Serializer of Warehouses object into JSON.
 */
class WarehousesSerializer extends PageableListSerializer {

    public WarehousesSerializer() {
        super(ROOT_NODE);
    }

}
