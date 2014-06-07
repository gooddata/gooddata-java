package com.gooddata.dataset;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PullTaskStatusTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/pullTaskStatus.json");
        final PullTaskStatus taskStatus = new ObjectMapper().readValue(stream, PullTaskStatus.class);

        assertThat(taskStatus.getStatus(), is("WARNING"));
    }
}
