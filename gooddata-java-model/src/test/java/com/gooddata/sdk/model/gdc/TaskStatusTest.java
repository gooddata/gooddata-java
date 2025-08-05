/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.gdc;

import com.gooddata.sdk.common.gdc.GdcError;
import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class TaskStatusTest {

    @Test
    public void testDeser() throws Exception {
        final TaskStatus status = readObjectFromResource("/gdc/task-status.json", TaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
    }

    @Test
    public void testDeserMessages() throws Exception {
        final TaskStatus status = readObjectFromResource("/model/maql-ddl-task-status-fail.json", TaskStatus.class);
        assertThat(status.getStatus(), is("ERROR"));
        assertThat(status.getPollUri(), is("/gdc/md/PROJECT_ID/tasks/TASK_ID/status"));
        assertThat(status.getMessages(), hasSize(1));
        final GdcError error = status.getMessages().iterator().next();
        assertThat(error.getFormattedMessage(), is("The object (attr.person.id) doesn't exists."));
    }

    @Test
    public void testSerialize() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new TaskStatus("OK", "foo"));
        final TaskStatus status = OBJECT_MAPPER.readValue(json, TaskStatus.class);
        assertThat(status.getStatus(), is("OK"));
        assertThat(status.getPollUri(), is("foo"));
    }

}