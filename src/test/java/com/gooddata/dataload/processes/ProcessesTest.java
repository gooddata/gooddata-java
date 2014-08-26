package com.gooddata.dataload.processes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ProcessesTest {

    @Test
    public void testDeserialization() throws Exception {
        final Processes processes = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/processes.json"), Processes.class);

        assertThat(processes, is(notNullValue()));
        assertThat(processes.getItems(), hasSize(1));
    }

}