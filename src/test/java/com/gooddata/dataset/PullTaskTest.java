package com.gooddata.dataset;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PullTaskTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/pullTask.json");
        final PullTask task = new ObjectMapper().readValue(stream, PullTask.class);

        assertThat(task.getUri(), is("/gdc/md/PROJECT/etl/task/ID"));
    }
}
