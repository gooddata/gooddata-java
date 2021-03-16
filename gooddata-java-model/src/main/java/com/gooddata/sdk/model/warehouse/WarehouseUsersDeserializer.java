/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * Deserializer of JSON into warehouse users object.
 */
class WarehouseUsersDeserializer extends PageDeserializer<WarehouseUsers, WarehouseUser> {

    protected WarehouseUsersDeserializer() {
        super(WarehouseUser.class);
    }

    @Override
    protected WarehouseUsers createPage(final List<WarehouseUser> items, final Paging paging, final Map<String, String> links) {
        return new WarehouseUsers(items, paging, links);
    }
}
