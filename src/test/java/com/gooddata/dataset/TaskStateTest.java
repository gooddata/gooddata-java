/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class TaskStateTest {

    @Test
    public void testDeserialize() throws IOException {
        final InputStream stream = getClass().getResourceAsStream("/dataset/taskStateOK.json");
        TaskState taskState = new ObjectMapper().readValue(stream, TaskState.class);
        assertThat(taskState.getStatus(), is("OK"));
        assertThat(taskState.getMessage(), is("ok message"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/taskStateOK.json");
        TaskState taskState = new ObjectMapper().readValue(stream, TaskState.class);

        assertThat(taskState.toString(), matchesPattern(TaskState.class.getSimpleName() + "\\[.*\\]"));
    }
}
