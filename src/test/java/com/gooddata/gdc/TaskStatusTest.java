/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TaskStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeser() throws Exception {
        final TaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/gdc/task-status.json"), TaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
    }

    @Test
    public void testDeserMessages() throws Exception {
        final TaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/model/maql-ddl-task-status-fail.json"), TaskStatus.class);
        assertThat(status.getStatus(), is("ERROR"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
        assertThat(status.getMessages(), hasSize(1));
        final GdcError error = status.getMessages().iterator().next();
        assertThat(error.getFormattedMessage(), is("The object (attr.person.id) doesn't exists."));
    }

    @Test
    public void testSerialize() throws Exception {
        final String json = MAPPER.writeValueAsString(new TaskStatus("OK", "foo"));
        final TaskStatus status = MAPPER.readValue(json, TaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("foo"));
    }

}