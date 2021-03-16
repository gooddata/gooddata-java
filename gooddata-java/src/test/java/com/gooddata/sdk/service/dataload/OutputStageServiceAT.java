/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.dataload.OutputStage;
import com.gooddata.sdk.model.project.Environment;
import com.gooddata.sdk.model.warehouse.Warehouse;
import com.gooddata.sdk.model.warehouse.WarehouseSchema;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.TimeUnit;

public class OutputStageServiceAT extends AbstractGoodDataAT {

    private static final String CLIENT_ID = "clientId";
    private static final String PREFIX = "prefix";

    private final Warehouse warehouse;
    private final WarehouseSchema warehouseSchema;

    public OutputStageServiceAT() {
        final String warehouseToken = getProperty("warehouseToken");
        final Warehouse wh = new Warehouse(title, warehouseToken);
        wh.setEnvironment(Environment.TESTING);
        warehouse = gd.getWarehouseService().createWarehouse(wh).get(60, TimeUnit.MINUTES);
        warehouseSchema = gd.getWarehouseService().getDefaultWarehouseSchema(warehouse);
    }

    @Test(groups = "output_stage", dependsOnGroups = {"warehouse", "project"})
    public void shouldReturnNullObjectWhenNoOutputStage() {
        final OutputStage outputStage = gd.getOutputStageService().getOutputStage(project);

        assertThat(outputStage.getSchemaUri(), is(nullValue()));
    }

    @Test(groups = "output_stage", dependsOnMethods = "shouldReturnNullObjectWhenNoOutputStage")
    public void shouldUpdateOutputStage() {
        final OutputStage outputStage = gd.getOutputStageService().getOutputStage(project);
        outputStage.setSchemaUri(warehouseSchema.getUri());
        outputStage.setClientId(CLIENT_ID);
        outputStage.setOutputStagePrefix(PREFIX);

        final OutputStage updateOutputStage = gd.getOutputStageService().updateOutputStage(outputStage);

        assertThat(updateOutputStage.getSchemaUri(), is(equalTo(warehouseSchema.getUri())));
        assertThat(updateOutputStage.getClientId(), is(equalTo(CLIENT_ID)));
        assertThat(updateOutputStage.getOutputStagePrefix(), is(equalTo(PREFIX)));
    }

    @Test(groups = "output_stage", dependsOnMethods = "shouldUpdateOutputStage")
    public void shouldUpdateOutputStageToNullValues() {
        final OutputStage outputStage = gd.getOutputStageService().getOutputStage(project);
        outputStage.setSchemaUri(null);
        outputStage.setClientId(null);
        outputStage.setOutputStagePrefix(null);

        final OutputStage updateOutputStage = gd.getOutputStageService().updateOutputStage(outputStage);

        assertThat(updateOutputStage.getSchemaUri(), is(nullValue()));
        assertThat(updateOutputStage.getClientId(), is(nullValue()));
        assertThat(updateOutputStage.getOutputStagePrefix(), is(nullValue()));
    }

    @AfterClass
    public void removeWarehouse() {
        if(warehouse != null) {
            gd.getWarehouseService().removeWarehouse(warehouse);
        }
    }
}
