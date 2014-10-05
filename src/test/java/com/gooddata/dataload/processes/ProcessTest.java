package com.gooddata.dataload.processes;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ProcessTest {


    @Test
    public void testDeserialization() throws Exception {
        final Process process = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/process.json"), Process.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getName(), is("testProcess"));
        assertThat(process.getType(), is("GROOVY"));
        assertThat(process.getExecutables(), hasSize(1));
        assertThat(process.getId(), is("processId"));
        assertThat(process.getExecutionsLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions"));

    }

    @Test
    public void testSerialization() {
        final Process process = new Process("testProcess", "GROOVY");
        assertThat(process, serializesToJson("/dataload/processes/process-input.json"));
    }
}