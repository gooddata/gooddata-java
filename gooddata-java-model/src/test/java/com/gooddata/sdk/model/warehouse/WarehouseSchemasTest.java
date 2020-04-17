/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

import org.testng.annotations.Test;

public class WarehouseSchemasTest {
    private final WarehouseSchemas warehouseSchemas = readObjectFromResource("/warehouse/schemas.json", WarehouseSchemas.class);

    @Test
    public void testDeserialization() throws Exception {
        assertThat(warehouseSchemas, notNullValue());
        assertThat(warehouseSchemas.getPageItems(), hasSize(1));
        assertThat(warehouseSchemas.getPageItems().get(0).getName(), is("default"));
    }
}
