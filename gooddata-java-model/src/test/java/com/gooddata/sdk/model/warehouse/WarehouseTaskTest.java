/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class WarehouseTaskTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserializePoll() throws Exception {
        final WarehouseTask warehouseTask = readObjectFromResource("/warehouse/warehouseTask-poll.json", WarehouseTask.class);
        assertThat(warehouseTask.getPollLink(), is("/gdc/datawarehouse/executions/executionId"));
        assertThat(warehouseTask.getPollUri(), is("/gdc/datawarehouse/executions/executionId"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserializeInstance() throws Exception {
        final WarehouseTask warehouseTask = readObjectFromResource("/warehouse/warehouseTask-finished.json", WarehouseTask.class);
        assertThat(warehouseTask.getWarehouseLink(), is("/gdc/datawarehouse/instances/instanceId"));
        assertThat(warehouseTask.getWarehouseUri(), is("/gdc/datawarehouse/instances/instanceId"));
    }

}
