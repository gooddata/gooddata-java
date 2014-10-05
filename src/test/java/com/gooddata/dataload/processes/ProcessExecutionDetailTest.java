package com.gooddata.dataload.processes;

import org.codehaus.jackson.map.ObjectMapper;
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
        assertThat(executionDetail.getCreated(), is("2014-02-24T19:00:35.999Z"));
        assertThat(executionDetail.getStarted(), is("2014-02-24T19:00:39.155Z"));
        assertThat(executionDetail.getUpdated(), is("2014-02-24T19:26:13.197Z"));
        assertThat(executionDetail.getFinished(), is("2014-02-24T19:26:13.060Z"));
        assertThat(executionDetail.getError(), notNullValue());
        assertThat(executionDetail.getError().getErrorCode(), is("executor.error"));
        assertThat(executionDetail.getError().getFormattedMessage(),
                is("Error message with some placeholders for parameters - like this one."));

    }


}