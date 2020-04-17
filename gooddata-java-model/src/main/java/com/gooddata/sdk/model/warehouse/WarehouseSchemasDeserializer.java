/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * Deserializer of WarehouseSchemas.
 */
class WarehouseSchemasDeserializer extends PageDeserializer<WarehouseSchemas, WarehouseSchema> {

    protected WarehouseSchemasDeserializer() {
        super(WarehouseSchema.class);
    }

    @Override
    protected WarehouseSchemas createPage(final List<WarehouseSchema> items, final Paging paging, final Map<String, String> links) {
        return new WarehouseSchemas(items, paging, links);
    }
}
