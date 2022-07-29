/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

class WarehousesDeserializer extends PageDeserializer<Warehouses, Warehouse> {

    protected WarehousesDeserializer() {
        super(Warehouse.class);
    }

    @Override
    protected Warehouses createPage(final List<Warehouse> items, final Paging paging, final Map<String, String> links) {
        return new Warehouses(items, paging, links);
    }
}
