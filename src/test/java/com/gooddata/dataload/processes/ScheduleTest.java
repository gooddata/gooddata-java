package com.gooddata.dataload.processes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class ScheduleTest {

    private static final String EXECUTABLE = "Twitter/graph/twitter.grf";

    @Mock
    private DataloadProcess process;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(process.getExecutables()).thenReturn(Collections.singleton(EXECUTABLE));
        when(process.getId()).thenReturn("process_id");
        doThrow(new IllegalArgumentException("wrong executable")).when(process).validateExecutable(Matchers.argThat(not(is(EXECUTABLE))));
    }

    @Test
    public void testDeserialization() throws Exception {
        final Schedule schedule = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/dataload/processes/schedule.json"), Schedule.class);

        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getId(), is("SCHEDULE_ID"));
        assertThat(schedule.getType(), is("MSETL"));
        assertThat(schedule.getState(), is("ENABLED"));
        assertThat(schedule.isEnabled(), is(true));
        assertThat(schedule.getCron(), is("0 0 * * *"));
        assertThat(schedule.getTimezone(), is("UTC"));
        assertThat(schedule.getConsecutiveFailedExecutionCount(), is(0));
        assertThat(schedule.getProcessId(), is("process_id"));
        assertThat(schedule.getExecutable(), is(EXECUTABLE));
        assertThat(schedule.getNextExecutionTime(), is(DateTime.parse("2013-11-16T00:00:00.000Z")));
        assertThat(schedule.getUri(), is("/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID"));
    }

    @Test
    public void testSerialization() {
        final Schedule schedule = new Schedule(process, EXECUTABLE, "0 0 * * *");
        assertThat(schedule, serializesToJson("/dataload/processes/schedule-input.json"));
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

}