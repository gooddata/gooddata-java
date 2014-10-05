package com.gooddata.dataload.processes;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProcessExecutionTest {

    @Test
    public void testSerialization() throws Exception {
        final Map<String,String> params = new HashMap<>();
        final Map<String,String> hidden = new HashMap<>();
        params.put("PARAM1", "VALUE1");
        params.put("PARAM2", "VALUE2");
        hidden.put("HIDDEN_PARAM1", "SENSITIVE_VALUE1");
        hidden.put("HIDDEN_PARAM2", "SENSITIVE_VALUE2");

        final Process process = new ObjectMapper().readValue(getClass().getResourceAsStream("/dataload/processes/process.json"), Process.class);

        final ProcessExecution execution = new ProcessExecution(process, "test.groovy", params, hidden);
        assertThat(execution, serializesToJson("/dataload/processes/execution.json"));
    }

}