/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.Schedule;
import com.gooddata.project.Environment;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.WarehouseSchema;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import static com.gooddata.dataload.processes.ProcessType.DATALOAD;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
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
    public void scheduleDataloadProcess() {
        final DataloadProcess dataloadProcess = gd.getProcessService()
                .listProcesses(project)
                .stream()
                .filter(e -> e.getType().equals(DATALOAD.name()))
                .findFirst()
                .get();

        Schedule schedule = gd.getProcessService().createSchedule(project, new Schedule(dataloadProcess, "0 0 * * *", true));

        assertThat(schedule, is(notNullValue()));
    }

    @Test(groups = "output_stage", dependsOnMethods = "scheduleDataloadProcess")
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
