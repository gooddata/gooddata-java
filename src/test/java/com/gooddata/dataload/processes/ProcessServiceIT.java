/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.FutureResult;
import com.gooddata.collections.PageableList;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class ProcessServiceIT extends AbstractGoodDataIT {

    private static final String PROCESSES_PATH = DataloadProcesses.TEMPLATE.expand("PROJECT_ID").toString();
    private static final String PROCESS_ID = "processId";
    private static final String PROCESS_PATH = DataloadProcess.TEMPLATE.expand("PROJECT_ID", PROCESS_ID).toString();
    private static final String PROCESS_SOURCE_PATH = PROCESS_PATH + "/source";
    private static final String EXECUTIONS_PATH = PROCESS_PATH + "/executions";
    private static final String EXECUTION_PATH = EXECUTIONS_PATH + "/executionId";
    private static final String EXECUTION_DETAIL_PATH = EXECUTION_PATH + "/detail";
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String SCHEDULES_PATH = Schedules.TEMPLATE.expand(PROJECT_ID).toString();
    private static final String SCHEDULE_ID = "SCHEDULE_ID";
    private static final String SCHEDULE_PATH = Schedule.TEMPLATE.expand(PROJECT_ID, SCHEDULE_ID).toString();
    private static final String EXECUTABLE = "test.groovy";
    private static final String PROCESS_DEPLOYMENT_POLLING_URI = "/gdc/projects/PROJECT_ID/dataload/processesDeploy/uri";
    private static final String EXECUTION_ID = "EXECUTION_ID";

    private Project project;

    private DataloadProcess process;

    private Schedule schedule;

    private File file;

    @BeforeClass
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
        process = readObjectFromResource("/dataload/processes/process.json", DataloadProcess.class);
        schedule = readObjectFromResource("/dataload/processes/schedule.json", Schedule.class);
        file = File.createTempFile("test", ".groovy");
    }

    @Test
    public void shouldCreateProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/process.json"))
                .withStatus(201);

        final DataloadProcess process = gd.getProcessService().createProcess(project, new DataloadProcess("testProcess", "GROOVY"), file);
        assertThat(process, notNullValue());
        assertThat(process.getExecutables(), contains("test.groovy"));
    }

    @Test
    public void shouldCreateProcessWithoutData() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/processWithoutData.json"))
                .withStatus(201);

        final String processName = "dataloadProcess";
        final DataloadProcess process = gd.getProcessService().createProcess(project,
                new DataloadProcess(processName, ProcessType.DATALOAD));
        assertThat(process, notNullValue());
        assertThat(process.getName(), is(processName));
        assertThat(process.getType(), is(ProcessType.DATALOAD.toString()));
        assertThat(process.getExecutables(), nullValue());
    }

    @Test
    public void shouldListProcesses() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/processes.json"))
                .withStatus(200);

        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, notNullValue());
        assertThat(processes, hasSize(1));
    }

    @Test
    public void shouldGetProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESS_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/process.json"))
                .withStatus(200);

        final DataloadProcess process = gd.getProcessService().getProcessById(project, PROCESS_ID);
        assertThat(process, notNullValue());
        assertThat(process.getName(), is("testProcess"));
    }

    @Test
    public void shouldRemoveProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(PROCESS_PATH)
            .respond()
                .withStatus(204);

        gd.getProcessService().removeProcess(process);
    }

    @Test
    public void shouldGetProcessSource() throws Exception {
        final String processDownloadLink = "/processSourceUri";
        final String processSource = "processSource";

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESS_SOURCE_PATH)
            .respond()
                .withHeader("Location", processDownloadLink)
                .withStatus(303);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(processDownloadLink)
            .respond()
                .withBody(processSource)
                .withStatus(200);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        gd.getProcessService().getProcessSource(process, outputStream);
        assertThat(outputStream.toString(), is(processSource));
    }

    @Test
    public void shouldGetExecutionLog() throws Exception {
        final String logDownloadLink = EXECUTION_PATH + "/log";
        final String log = "log";

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(logDownloadLink)
            .respond()
                .withBody(log)
                .withStatus(200);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final ProcessExecutionDetail executionDetail = readObjectFromResource("/dataload/processes/executionDetail.json", ProcessExecutionDetail.class);
        gd.getProcessService().getExecutionLog(executionDetail, outputStream);
        assertThat(outputStream.toString(), is(log));
    }

    @Test
    public void shouldExecuteProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXECUTIONS_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/executionTask.json"))
                .withHeader("Location", EXECUTION_PATH)
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_PATH)
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(204);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_DETAIL_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/executionDetail-success.json"))
                .withStatus(200);

        final FutureResult<ProcessExecutionDetail> result = gd.getProcessService().executeProcess(new ProcessExecution(process, "test.groovy"));
        final ProcessExecutionDetail executionDetail = result.get();

        assertJsonEquals(readObjectFromResource("/dataload/processes/executionDetail-success.json", ProcessExecutionDetail.class), executionDetail);
    }

    @Test(expectedExceptions = ProcessExecutionException.class)
    public void shouldThrowOnExecuteProcessError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXECUTIONS_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/executionTask.json"))
                .withHeader("Location", EXECUTION_PATH)
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_PATH)
            .respond()
                .withStatus(410)
        ;
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_DETAIL_PATH)
            .respond()
                .withBody(readFromResource("/dataload/processes/executionDetail.json"))
                .withStatus(200);

        gd.getProcessService().executeProcess(new ProcessExecution(process, "test.groovy")).get();
    }

    @Test
    public void shouldCreateSchedule() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(SCHEDULES_PATH)
                .respond()
                .withBody(readFromResource("/dataload/processes/schedule.json"))
                .withStatus(201);

        final Schedule schedule = gd.getProcessService().createSchedule(project, new Schedule(process, EXECUTABLE, "0 0 * * *"));
        assertThat(schedule, notNullValue());
        assertThat(schedule.getId(), is(SCHEDULE_ID));
    }

    @Test
    public void shouldUpdateSchedule() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(SCHEDULE_PATH)
                .respond()
                .withBody(readFromResource("/dataload/processes/schedule-disabled.json"))
                .withStatus(200);

        schedule.setState(ScheduleState.DISABLED);
        final Schedule updated = gd.getProcessService().updateSchedule(schedule);
        assertThat(updated, notNullValue());
        assertThat(updated.isEnabled(), is(false));
    }

    @Test
    public void shouldListPagedSchedules() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SCHEDULES_PATH)
                .respond()
                .withBody(readFromResource("/dataload/processes/schedules_page1.json"))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SCHEDULES_PATH)
                .havingQueryStringEqualTo("offset=1&limit=1")
                .respond()
                .withBody(readFromResource("/dataload/processes/schedules_page2.json"))
                .withStatus(200);

        final PageableList<Schedule> firstPage = gd.getProcessService().listSchedules(project);
        assertThat(firstPage, notNullValue());
        assertThat(firstPage, hasSize(1));
        assertThat(firstPage.getNextPage(), notNullValue());
        assertThat(firstPage.getNextPage().getPageUri(null).toString(), is("/gdc/projects/PROJECT_ID/schedules?offset=1&limit=1"));

        final PageableList<Schedule> secondPage = gd.getProcessService().listSchedules(project, firstPage.getNextPage());
        assertThat(secondPage, notNullValue());
        assertThat(secondPage, hasSize(1));
        assertThat(secondPage.getNextPage(), nullValue());
    }

    @Test
    public void shouldGetSchedule() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SCHEDULE_PATH)
                .respond()
                .withBody(readFromResource("/dataload/processes/schedule.json"))
                .withStatus(200);

        final Schedule schedule = gd.getProcessService().getScheduleById(project, SCHEDULE_ID);
        assertThat(schedule, notNullValue());
        assertThat(schedule.isEnabled(), is(true));
    }

    @Test
    public void shouldRemoveSchedule() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(SCHEDULE_PATH)
                .respond()
                .withStatus(204);

        gd.getProcessService().removeSchedule(schedule);
    }

    @Test
    public void shouldCreateProcessFromAppstorePackageRegistryMiss() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROCESSES_PATH)
             .respond()
                .withBody(readFromResource("/dataload/processes/asyncTask.json"))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESS_DEPLOYMENT_POLLING_URI)
             .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataload/processes/appstoreProcess.json"));
        DataloadProcess process = gd.getProcessService().createProcessFromAppstore(project, new DataloadProcess("appstoreProcess", ProcessType.RUBY.toString(),
                "${PUBLIC_APPSTORE}:tag/prodigy-testing:/test/rubyHello")).get();
        assertThat(process, notNullValue());
    }

    @Test
    public void shouldCreateProcessFromAppstorePackageRegistryHit() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataload/processes/appstoreProcess.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESS_PATH)
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataload/processes/appstoreProcess.json"));

        DataloadProcess process = gd.getProcessService().createProcessFromAppstore(project, new DataloadProcess("appstoreProcess", ProcessType.RUBY.toString(),
                "${PUBLIC_APPSTORE}:tag/prodigy-testing:/test/rubyHello")).get();
        assertThat(process, notNullValue());
    }

    @Test
    public void shouldUpdateProcessFromAppstore() {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(PROCESS_PATH)
             .respond()
                .withBody(readFromResource("/dataload/processes/asyncTask.json"))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESS_DEPLOYMENT_POLLING_URI)
             .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataload/processes/appstoreProcess.json"));

        process.setPath("${PUBLIC_APPSTORE}:tag/prodigy-testing:/test/rubyHello");
        DataloadProcess updatedProcess = gd.getProcessService().updateProcessFromAppstore(process).get();
        assertThat(updatedProcess, notNullValue());
        assertThat(updatedProcess.getType(), is(equalTo(ProcessType.RUBY.toString())));
        assertThat(updatedProcess.getExecutables(), contains("hello.rb") );
    }

    @Test
    public void shouldExecuteSchedule() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(schedule.getExecutionsUri())
             .respond()
                .withBody(readFromResource("/dataload/processes/scheduleExecution.json"))
                .withStatus(200);

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ScheduleExecution.TEMPLATE.expand(PROJECT_ID, SCHEDULE_ID, EXECUTION_ID).toString())
              .respond()
                .withBody(readFromResource("/dataload/processes/scheduleExecution.json"))
                .withStatus(200);

        FutureResult<ScheduleExecution> futureResult = gd.getProcessService().executeSchedule(schedule);
        ScheduleExecution scheduleExecution = futureResult.get();

        assertThat(scheduleExecution.getStatus(), is("OK"));
    }

    @Test(expectedExceptions = ScheduleExecutionException.class)
    public void shouldNotExecuteSchedule() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(schedule.getExecutionsUri())
                .respond()
                .withStatus(409);

        gd.getProcessService().executeSchedule(schedule);
    }
}
