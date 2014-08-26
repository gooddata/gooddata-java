package com.gooddata.dataload.processes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ProcessExecutionDetailTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeserialization() throws Exception {
        final ProcessExecutionDetail executionDetail = MAPPER.readValue(getClass().getResourceAsStream("/dataload/processes/executionDetail.json"),
                ProcessExecutionDetail.class);
        assertNotNull(executionDetail);
        assertThat(executionDetail.getStatus(), is("ERROR"));
        assertThat(executionDetail.getCreated(), is("2014-02-24T19:00:35.999Z"));
        assertThat(executionDetail.getStarted(), is("2014-02-24T19:00:39.155Z"));
        assertThat(executionDetail.getUpdated(), is("2014-02-24T19:26:13.197Z"));
        assertThat(executionDetail.getFinished(), is("2014-02-24T19:26:13.060Z"));
        assertNotNull(executionDetail.getError());
        assertThat(executionDetail.getError().getErrorCode(), is("executor.error"));
        assertThat(executionDetail.getError().getFormattedMessage(),
                is("Error message with some placeholders for parameters - like this one."));

    }


}