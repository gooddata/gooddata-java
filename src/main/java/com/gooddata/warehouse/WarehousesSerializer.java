/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
