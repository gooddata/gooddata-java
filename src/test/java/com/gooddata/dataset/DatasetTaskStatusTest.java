package com.gooddata.dataset;

import com.gooddata.gdc.GdcError;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class DatasetTaskStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeser() throws Exception {
        final DatasetTaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/model/maql-ddl-task-status.json"), DatasetTaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
    }

    @Test
    public void testDeserMessages() throws Exception {
        final DatasetTaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/model/maql-ddl-task-status-fail.json"), DatasetTaskStatus.class);
        assertThat(status.getStatus(), is("ERROR"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
        assertThat(status.getMessages(), hasSize(1));
        final GdcError error = status.getMessages().iterator().next();
        assertThat(error.getFormattedMessage(), is("The object (attr.person.id) doesn't exists."));
    }

    @Test
    public void testSerialize() throws Exception {
        final String json = MAPPER.writeValueAsString(new DatasetTaskStatus("OK", "foo"));
        final DatasetTaskStatus status = MAPPER.readValue(json, DatasetTaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("foo"));
    }
}
