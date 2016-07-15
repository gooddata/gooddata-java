package com.gooddata.dataload.processes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class ProcessExecutionDetailTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeserialization() throws Exception {
        final ProcessExecutionDetail executionDetail = MAPPER.readValue(getClass().getResourceAsStream("/dataload/processes/executionDetail.json"),
                ProcessExecutionDetail.class);
        assertThat(executionDetail, notNullValue());
        assertThat(executionDetail.getStatus(), is("ERROR"));
        assertThat(executionDetail.getCreated(), is(new DateTime(2014, 2, 24, 19, 0, 35, 999, DateTimeZone.UTC)));
        assertThat(executionDetail.getStarted(), is(new DateTime(2014, 2, 24, 19, 0, 39, 155, DateTimeZone.UTC)));
        assertThat(executionDetail.getUpdated(), is(new DateTime(2014, 2, 24, 19, 26, 13, 197, DateTimeZone.UTC)));
        assertThat(executionDetail.getFinished(), is(new DateTime(2014, 2, 24, 19, 26, 13, 60, DateTimeZone.UTC)));
        assertThat(executionDetail.getError(), notNullValue());
        assertThat(executionDetail.getError().getErrorCode(), is("executor.error"));
        assertThat(executionDetail.getError().getFormattedMessage(),
                is("Error message with some placeholders for parameters - like this one."));
        assertThat(executionDetail.getUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/detail"));
        assertThat(executionDetail.getLogUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId/log"));
        assertThat(executionDetail.getExecutionUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions/executionId"));
    }

}