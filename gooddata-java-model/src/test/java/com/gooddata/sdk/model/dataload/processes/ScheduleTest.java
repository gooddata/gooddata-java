/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.stream.Stream;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
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
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.junit.jupiter.api.Assertions.assertThrows;



public class ScheduleTest {

    private static final String EXECUTABLE = "Twitter/graph/twitter.grf";

    @Mock
    private static DataloadProcess process;

    @Mock
    private Schedule triggerSchedule;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();

        when(process.getExecutables()).thenReturn(Collections.singleton(EXECUTABLE));
        when(process.getId()).thenReturn("process_id");
        when(triggerSchedule.getId()).thenReturn("schedule_id");
        doThrow(new IllegalArgumentException("wrong executable")).when(process).validateExecutable(argThat(not(is(EXECUTABLE))));
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
        assertThat(schedule.getNextExecutionTime(), is(ZonedDateTime.parse("2013-11-16T00:00:00.000Z")));
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
        assertThat(schedule.getNextExecutionTime(), is(ZonedDateTime.parse("2013-11-16T00:00:00.000Z")));
        assertThat(schedule.getUri(), is("/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID"));
        assertThat(schedule.getTriggerScheduleId(), is("trigger_schedule_id"));
    }

    @Test
    public void testSerialization() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule, jsonEquals(resource("dataload/processes/schedule-input.json")));
    }

    @Test
    public void testTriggeredScheduleSerialization() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, triggerSchedule);
        assertThat(schedule, jsonEquals(resource("dataload/processes/schedule-triggered-input.json")));
    }

    @Test
    public void testSerializationWithAllFields() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        schedule.setReschedule(Duration.ofMinutes(26));
        schedule.setName("scheduleName");

        assertThat(schedule, jsonEquals(resource("dataload/processes/schedule-input-all-fields.json")));
    }


    static Stream<Arguments> scheduleParams() {

        return Stream.of(
                Arguments.of(null, EXECUTABLE, "0 0 * * *", "process can't be null"),
                Arguments.of(process, "garbage", "0 0 * * *", "wrong executable"),
                Arguments.of(process, EXECUTABLE, "", "cron can't be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("scheduleParams")
    void testIncorrectConstructorParams(DataloadProcess process, String executable, String cron, String message) {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Schedule(process, executable, cron)
        );
        assertThat(ex.getMessage(), containsString(message));
    }

    @Test 
    void testSetCron() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Schedule(process, EXECUTABLE, "0 0 * * *").setCron("");
        });
        assertThat(exception.getMessage(), containsString("cron can't be empty"));
    }

    @Test
    public void testSetTimezoneAnyString() {
        new Schedule(process, EXECUTABLE, "0 0 * * *").setTimezone("some nonsense garbage to prove a point");
    }

    @Test
    void testSetTimezoneObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Schedule(process, EXECUTABLE, "0 0 * * *").setTimezone((ZonedDateTime) null);
        });
        assertThat(exception.getMessage(), containsString("timezone can't be null"));
    }

    @Test
    public void testSetProcessId() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        when(process.getId()).thenReturn("other");
        assertThat(schedule.getProcessId(), is("process_id"));
        schedule.setProcessId(process);
        assertThat(schedule.getProcessId(), is("other"));
    }


    @Test
    void testSetExecutable() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        schedule.setExecutable(process, EXECUTABLE);
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            schedule.setExecutable(process, "garbage");
        });
        assertThat(exception.getMessage(), containsString("wrong executable"));
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
        final Duration duration = Duration.ofMinutes(26);
        schedule.setReschedule(duration);

        assertThat(schedule.getRescheduleInMinutes(), is(equalTo(26)));
        assertThat(schedule.getReschedule(), is(equalTo(duration)));
    }

    @Test
    public void testRescheduleTooLowDuration() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        schedule.setReschedule(Duration.ofSeconds(26));

        assertThat(schedule.getRescheduleInMinutes(), is(equalTo(0)));
        assertThat(schedule.getReschedule(), is(equalTo(Duration.ZERO)));
    }

    @Test
    void testCreateTriggeredScheduleWithNotCreatedSchedule() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThrows(IllegalArgumentException.class, () -> new Schedule(process, EXECUTABLE, schedule));
    }

    @Test
    public void testToStringFormat() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");

        assertThat(schedule.toString(), matchesPattern(Schedule.class.getSimpleName() + "\\[.*\\]"));
    }
}