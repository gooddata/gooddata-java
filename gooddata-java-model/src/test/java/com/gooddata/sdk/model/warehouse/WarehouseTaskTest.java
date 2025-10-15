/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class WarehouseTaskTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserializePoll() throws Exception {
        final WarehouseTask warehouseTask = readObjectFromResource("/warehouse/warehouseTask-poll.json", WarehouseTask.class);
        assertThat(warehouseTask.getPollUri(), is("/gdc/datawarehouse/executions/executionId"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserializeInstance() throws Exception {
        final WarehouseTask warehouseTask = readObjectFromResource("/warehouse/warehouseTask-finished.json", WarehouseTask.class);
        assertThat(warehouseTask.getWarehouseUri(), is("/gdc/datawarehouse/instances/instanceId"));
    }

}

