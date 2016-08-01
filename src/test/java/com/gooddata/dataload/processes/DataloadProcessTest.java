package com.gooddata.dataload.processes;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

public class DataloadProcessTest {


    @Test
    public void testDeserialization() throws Exception {
        final DataloadProcess process = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/process.json"), DataloadProcess.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getName(), is("testProcess"));
        assertThat(process.getType(), is("GROOVY"));
        assertThat(process.getExecutables(), hasSize(1));
        assertThat(process.getId(), is("processId"));
        assertThat(process.getUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId"));
        assertThat(process.getExecutionsLink(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions"));
        assertThat(process.getExecutionsUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions"));
        assertThat(process.getSourceUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/source"));
    }

    @Test
    public void testSerialization() {
        final DataloadProcess process = new DataloadProcess("testProcess", "GROOVY");
        assertThat(process, serializesToJson("/dataload/processes/process-input.json"));

        final DataloadProcess processWithPath = new DataloadProcess("testProcess", "GROOVY", "/uploads/process.zip");
        assertThat(processWithPath, serializesToJson("/dataload/processes/process-input-withPath.json"));
    }
}