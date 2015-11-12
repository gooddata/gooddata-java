package com.gooddata.warehouse;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class WarehouseTaskTest {

    @Test
    public void testDeserializePoll() throws Exception {
        final WarehouseTask warehouseTask = deserialize("/warehouse/warehouseTask-poll.json");
        assertThat(warehouseTask.getPollLink(), is("/gdc/datawarehouse/executions/executionId"));
    }

    @Test
    public void testDeserializeInstance() throws Exception {
        final WarehouseTask warehouseTask = deserialize("/warehouse/warehouseTask-finished.json");
        assertThat(warehouseTask.getWarehouseLink(), is("/gdc/datawarehouse/instances/instanceId"));
    }

    private WarehouseTask deserialize(String path) throws Exception {
        final InputStream stream = getClass().getResourceAsStream(path);
        return new ObjectMapper().readValue(stream, WarehouseTask.class);
    }
}