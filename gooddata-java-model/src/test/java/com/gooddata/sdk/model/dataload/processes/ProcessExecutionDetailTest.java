/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProcessExecutionDetailTest {

    @Test
    public void testDeserialization() throws Exception {
        final ProcessExecutionDetail executionDetail = readObjectFromResource("/dataload/processes/executionDetail.json",
                ProcessExecutionDetail.class);
        assertThat(executionDetail, notNullValue());
        assertThat(executionDetail.getStatus(), is("ERROR"));
        assertThat(executionDetail.getCreated(), is(ZonedDateTime.of(2014, 2, 24, 19, 0, 35, 999000000, UTC)));
        assertThat(executionDetail.getStarted(), is(ZonedDateTime.of(2014, 2, 24, 19, 0, 39, 155000000, UTC)));
        assertThat(executionDetail.getUpdated(), is(ZonedDateTime.of(2014, 2, 24, 19, 26, 13, 197000000, UTC)));
        assertThat(executionDetail.getFinished(), is(ZonedDateTime.of(2014, 2, 24, 19, 26, 13, 60000000, UTC)));
        assertThat(executionDetail.getError(), notNullValue());
        assertThat(executionDetail.getError().getErrorCode(), is("executor.error"));
        assertThat(executionDetail.getError().getFormattedMessage(),
                is("Error message with some placeholders for parameters - like this one."));
        assertThat(executionDetail.getUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
        assertThat(executionDetail.getLogUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/log"));
        assertThat(executionDetail.getExecutionUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ProcessExecutionDetail executionDetail = readObjectFromResource("/dataload/processes/executionDetail.json",
                ProcessExecutionDetail.class);

        assertThat(executionDetail.toString(), matchesPattern(ProcessExecutionDetail.class.getSimpleName() + "\\[.*\\]"));
    }

}