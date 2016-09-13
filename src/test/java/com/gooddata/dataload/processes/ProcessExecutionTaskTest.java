/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

public class ProcessExecutionTaskTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialize() throws Exception {
        final ProcessExecutionTask task = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/executionTask.json"), ProcessExecutionTask.class);
        assertThat(task.getPollLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
        assertThat(task.getPollUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
        assertThat(task.getDetailLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
        assertThat(task.getDetailUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
    }
}