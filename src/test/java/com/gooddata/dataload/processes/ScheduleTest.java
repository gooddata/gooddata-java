/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class ScheduleTest {

    private static final String EXECUTABLE = "Twitter/graph/twitter.grf";

    @Mock
    private DataloadProcess process;

    @Mock
    private Schedule triggerSchedule;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(process.getExecutables()).thenReturn(Collections.singleton(EXECUTABLE));
        when(process.getId()).thenReturn("process_id");
        when(triggerSchedule.getId()).thenReturn("schedule_id");
        doThrow(new IllegalArgumentException("wrong executable")).when(process).validateExecutable(Matchers.argThat(not(is(EXECUTABLE))));
    }

    @Test
    public void testDeserialization() throws Exception {
        final Schedule schedule = readObjectFromResource("/dataload/processes/schedule.json", Schedule.class);

        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getId(), is("SCHEDULE_ID"));
        assertThat(schedule.getType(), is("MSETL"));
        assertThat(schedule.getState(), is("ENABLED"));
        assertThat(schedule.isEnabled(), is(true));
        assertThat(schedule.getCron(), is("0 0 * * *"));
        assertThat(schedule.getTimezone(), is("UTC"));
        assertThat(schedule.getRescheduleInMinutes(), is(26));
        assertThat(schedule.getConsecutiveFailedExecutionCount(), is(0));
        assertThat(schedule.getProcessId(), is("process_id"));
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        assertThat(schedule.getNextExecutionTime(), is(DateTime.parse("2013-11-16T00:00:00.000Z")));
        assertThat(schedule.getUri(), is("/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID"));
        assertThat(schedule.getName(), is("scheduleName"));
    }

    @Test
    public void testTriggeredScheduleDeserialization() throws Exception {
        final Schedule schedule = readObjectFromResource("/dataload/processes/schedule-triggered.json", Schedule.class);

        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getId(), is("SCHEDULE_ID"));
        assertThat(schedule.getType(), is("MSETL"));
        assertThat(schedule.getState(), is("DISABLED"));
        assertThat(schedule.isEnabled(), is(false));
        assertThat(schedule.getTimezone(), is("UTC"));
        assertThat(schedule.getConsecutiveFailedExecutionCount(), is(0));
        assertThat(schedule.getProcessId(), is("process_id"));
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        assertThat(schedule.getNextExecutionTime(), is(DateTime.parse("2013-11-16T00:00:00.000Z")));
        assertThat(schedule.getUri(), is("/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID"));
        assertThat(schedule.getTriggerScheduleId(), is("trigger_schedule_id"));
    }

    @Test
    public void testSerialization() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule, serializesToJson("/dataload/processes/schedule-input.json"));
    }

    @Test
    public void testTriggeredScheduleSerialization() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, triggerSchedule);
        assertThat(schedule, serializesToJson("/dataload/processes/schedule-triggered-input.json"));
    }

    @Test
    public void testSerializationWithAllFields() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        schedule.setReschedule(Duration.standardMinutes(26));
        schedule.setName("scheduleName");

        assertThat(schedule, serializesToJson("/dataload/processes/schedule-input-all-fields.json"));
    }

    @DataProvider(name = "scheduleParams")
    public Object[][] scheduleParams() {
        return new Object[][] {
                new Object[] {null, EXECUTABLE, "0 0 * * *", "process"},
                new Object[] {process, "garbage", "0 0 * * *", "wrong executable"},
                new Object[] {process, EXECUTABLE, "", "cron can't be empty"}
        };
    }

    @Test(dataProvider = "scheduleParams")
    public void testIncorrectConstructorParams(final DataloadProcess process, final String executable,
                                               final String cron, final String message) {
        try {
            new Schedule(process, executable, cron);
            fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString(message));
        }
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*cron can't be empty.*"
    )
    public void testSetCron() {
        new Schedule(process, EXECUTABLE, "0 0 * * *").setCron("");
    }

    @Test
    public void testSetTimezoneAnyString() {
        new Schedule(process, EXECUTABLE, "0 0 * * *").setTimezone("some nonsense garbage to prove a point");
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*timezone can't be null.*"
    )
    public void testSetTimezoneObject() {
        new Schedule(process, EXECUTABLE, "0 0 * * *").setTimezone((DateTimeZone) null);
    }

    @Test
    public void testSetProcessId() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        when(process.getId()).thenReturn("other");
        assertThat(schedule.getProcessId(), is("process_id"));
        schedule.setProcessId(process);
        assertThat(schedule.getProcessId(), is("other"));
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*wrong executable.*"
    )
    public void testSetExecutable() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        schedule.setExecutable(process, EXECUTABLE);
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        schedule.setExecutable(process, "garbage");
    }

    @Test
    public void testCustomParams() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule.getProcessId(), is("process_id"));

        final int originalParamsSize = schedule.getParams().size();
        schedule.addParam("myParam", "value");

        assertThat(schedule.getParams(), hasEntry("myParam", "value"));
        assertThat(schedule.getParams().size(), is(originalParamsSize + 1));

        schedule.removeParam("myParam");
        assertThat(schedule.getParams(), not(hasEntry("myParam", "value")));
        assertThat(schedule.getParams().size(), is(originalParamsSize));
    }

    @Test
    public void testReschedule() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        final Duration duration = Duration.standardMinutes(26);
        schedule.setReschedule(duration);

        assertThat(schedule.getRescheduleInMinutes(), is(equalTo(26)));
        assertThat(schedule.getReschedule(), is(equalTo(duration)));
    }

    @Test
    public void testRescheduleTooLowDuration() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        schedule.setReschedule(Duration.standardSeconds(26));

        assertThat(schedule.getRescheduleInMinutes(), is(equalTo(0)));
        assertThat(schedule.getReschedule(), is(equalTo(Duration.ZERO)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateTriggeredScheduleWithNotCreatedSchedule() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        new Schedule(process, EXECUTABLE, schedule);
    }

    @Test
    public void testToStringFormat() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");

        assertThat(schedule.toString(), matchesPattern(Schedule.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializeSynchronizeAll() {
        final Schedule schedule = new Schedule(process, "0 0 * * *", true);

        assertThat(schedule, serializesToJson("/dataload/processes/scheduleSynchronizeAll.json"));
    }

    @Test
    public void testDeserializeSynchronizeAll() {
        final Schedule schedule = readObjectFromResource("/dataload/processes/scheduleSynchronizeAll.json", Schedule.class);

        assertThat(schedule.isSynchronizeAll(), is(true));
    }
}