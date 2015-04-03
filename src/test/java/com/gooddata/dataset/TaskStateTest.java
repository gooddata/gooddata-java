/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.dataset;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TaskStateTest {

    @Test
    public void testDeserialize() throws IOException {
        final InputStream stream = getClass().getResourceAsStream("/dataset/taskStateOK.json");
        TaskState taskState = new ObjectMapper().readValue(stream, TaskState.class);
        assertThat(taskState.getStatus(), is("OK"));
        assertThat(taskState.getMessage(), is("ok message"));
    }
}
