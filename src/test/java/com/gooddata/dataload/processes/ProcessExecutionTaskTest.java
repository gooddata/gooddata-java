package com.gooddata.dataload.processes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ProcessExecutionTaskTest {

    @Test
    public void testDeserialize() throws Exception {
        final ProcessExecutionTask task = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/executionTask.json"), ProcessExecutionTask.class);
        assertThat(task.getPollLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
        assertThat(task.getDetailLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));

    }
}