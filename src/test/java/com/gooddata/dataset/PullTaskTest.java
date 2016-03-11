package com.gooddata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PullTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/pullTask.json");
        final PullTask task = new ObjectMapper().readValue(stream, PullTask.class);

        assertThat(task.getPollUri(), is("/gdc/md/PROJECT/tasks/task/ID/status"));
    }
}
