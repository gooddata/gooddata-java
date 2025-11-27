/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ProcessExecutionTaskTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialize() throws Exception {
        final ProcessExecutionTask task = readObjectFromResource("/dataload/processes/executionTask.json", ProcessExecutionTask.class);
        assertThat(task.getPollUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
        assertThat(task.getDetailUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
    }
}