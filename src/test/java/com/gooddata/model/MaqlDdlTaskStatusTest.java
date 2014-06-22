/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import com.gooddata.gdc.GdcError;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MaqlDdlTaskStatusTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testDeser() throws Exception {
        final MaqlDdlTaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/model/maql-ddl-task-status.json"), MaqlDdlTaskStatus.class);
        assertEquals("OK", status.getStatus());
        assertEquals("/gdc/md/PROJECT_ID/tasks/TASK_ID/status", status.getPollUri());
    }

    @Test
    public void testDeserMessages() throws Exception {
        final MaqlDdlTaskStatus status = MAPPER.readValue(getClass().getResourceAsStream("/model/maql-ddl-task-status-fail.json"), MaqlDdlTaskStatus.class);
        assertEquals("ERROR", status.getStatus());
        assertEquals("/gdc/md/PROJECT_ID/tasks/TASK_ID/status", status.getPollUri());
        assertThat(status.getMessages(), hasSize(1));
        final GdcError error = status.getMessages().iterator().next();
        assertThat(error.getFormattedMessage(), is("The object (attr.person.id) doesn't exists."));
    }
}
