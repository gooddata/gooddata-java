/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class DataloadProcessesTest {

    @Test
    public void testDeserialization() throws Exception {
        final DataloadProcesses processes = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/processes.json"), DataloadProcesses.class);

        assertThat(processes, is(notNullValue()));
        assertThat(processes.getItems(), hasSize(1));
    }

}