/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * Deserializer of WarehouseSchemas.
 */
class WarehouseSchemasDeserializer extends PageableListDeserializer<WarehouseSchemas, WarehouseSchema> {

    protected WarehouseSchemasDeserializer() {
        super(WarehouseSchema.class);
    }

    @Override
    protected WarehouseSchemas createList(final List<WarehouseSchema> items, final Paging paging, final Map<String, String> links) {
        return new WarehouseSchemas(items, paging, links);
    }
}
