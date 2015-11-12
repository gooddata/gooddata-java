package com.gooddata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PullTaskStatusTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/pullTaskStatus.json");
        final PullTaskStatus taskStatus = new ObjectMapper().readValue(stream, PullTaskStatus.class);

        assertThat(taskStatus.getStatus(), is("WARNING"));
    }
}
