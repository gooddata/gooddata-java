/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;


import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.HashMap;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class ScheduleExecutionTest {

    @Test
    public void testEmptyScheduleExecutionSerialization() throws Exception {
        final ScheduleExecution scheduleExecution = new ScheduleExecution();

        assertThat(scheduleExecution, jsonEquals(resource("dataload/processes/emptyScheduleExecution.json")));
    }

    @Test
    public void testDeserialization() throws Exception {
        final ScheduleExecution scheduleExecution = readObjectFromResource("/dataload/processes/scheduleExecution.json", ScheduleExecution.class);

        assertThat(scheduleExecution, notNullValue());
        assertThat(scheduleExecution.getLinks(), is(equalTo(new HashMap<String, String>() {{
            put("self", "/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID/executions/EXECUTION_ID");
        }})));
        assertThat(scheduleExecution.getStatus(), is("OK"));
        assertThat(scheduleExecution.getTrigger(), is("MANUAL"));
        assertThat(scheduleExecution.getProcessLastDeployedBy(), is("bear@gooddata.com"));
        assertThat(scheduleExecution.getCreated(), is(ZonedDateTime.of(2017, 5, 9, 21, 54, 50, 924000000, UTC)));
    }
}
