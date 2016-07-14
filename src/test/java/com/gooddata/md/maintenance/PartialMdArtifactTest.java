package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.gdc.UriResponse;
import org.testng.annotations.Test;

import java.io.InputStream;

public class PartialMdArtifactTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/md/maintenance/partialMDArtifact.json");
        final PartialMdArtifact partialMdArtifact = new ObjectMapper().readValue(input, PartialMdArtifact.class);

        assertThat(partialMdArtifact.getStatusUri(), is("/gdc/md/projectId/tasks/taskId/status"));
        assertThat(partialMdArtifact.getToken(), is("TOKEN123"));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdArtifact partialMdArtifact = new PartialMdArtifact(new UriResponse("/gdc/md/projectId/tasks/taskId/status"), "TOKEN123");

        assertThat(partialMdArtifact, serializesToJson("/md/maintenance/partialMDArtifact.json"));
    }
}