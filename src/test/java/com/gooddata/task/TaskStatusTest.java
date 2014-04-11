package com.gooddata.task;

import static org.junit.Assert.assertEquals;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * TODO
 */
public class TaskStatusTest {

    @Test
    public void testDeser() throws Exception {
        final TaskStatus taskStatus = new ObjectMapper().readValue("", TaskStatus.class);
        assertEquals("OK", taskStatus.getStatus());
        assertEquals("someURI", taskStatus.getUri());


    }
}
