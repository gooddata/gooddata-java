/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

class WarehousesDeserializer extends PageableListDeserializer<Warehouses, Warehouse> {

    protected WarehousesDeserializer() {
        super(Warehouse.class);
    }

    @Override
    protected Warehouses createList(final List<Warehouse> items, final Paging paging, final Map<String, String> links) {
        return new Warehouses(items, paging, links);
    }
}
