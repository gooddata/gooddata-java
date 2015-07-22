/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * Deserializer of JSON into warehouse users object.
 */
class WarehouseUsersDeserializer extends PageableListDeserializer<WarehouseUsers, WarehouseUser> {

    protected WarehouseUsersDeserializer() {
        super(WarehouseUser.class);
    }

    @Override
    protected WarehouseUsers createList(final List<WarehouseUser> items, final Paging paging, final Map<String, String> links) {
        return new WarehouseUsers(items, paging, links);
    }
}
