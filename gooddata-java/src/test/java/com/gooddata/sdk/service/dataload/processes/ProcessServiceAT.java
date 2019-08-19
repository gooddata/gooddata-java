/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.PageableList;
import com.gooddata.sdk.model.dataload.processes.DataloadProcess;
import com.gooddata.sdk.model.dataload.processes.ProcessExecution;
import com.gooddata.sdk.model.dataload.processes.ProcessExecutionDetail;
import com.gooddata.sdk.model.dataload.processes.ProcessType;
import com.gooddata.sdk.model.dataload.processes.Schedule;
import com.gooddata.sdk.model.dataload.processes.ScheduleExecution;
import com.gooddata.sdk.model.dataload.processes.ScheduleState;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.service.FutureResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsIterableContaining;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;

import static java.nio.file.Files.createTempDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Dataload processes acceptance tests.
 */
public class ProcessServiceAT extends AbstractGoodDataAT {

    private DataloadProcess process;
    private DataloadProcess processAppstore;
    private Schedule schedule;
    private Schedule triggeredSchedule;

    @Test(groups = "process", dependsOnGroups = "project")
    public void createProcess() throws Exception {
        final File dir = createTempDirectory("sdktest").toFile();
        try {
            copy("sdktest.grf", dir);
            copy("invalid.grf", dir);
            copy("workspace.prm", dir);
            process = gd.getProcessService().createProcess(project, new DataloadProcess(title, ProcessType.GRAPH), dir);
        } finally {
            FileUtils.deleteQuietly(dir);
        }
    }

    @Test(groups = "process", dependsOnMethods = "createProcess")
    public void createSchedule() {
        schedule = gd.getProcessService().createSchedule(project, new Schedule(process, "sdktest.grf", "0 0 * * *"));
        schedule.setReschedule(Duration.ofMinutes(15));
        schedule.setName("sdkTestSchedule");

        assertThat(schedule, notNullValue());
        assertThat(schedule.getExecutable(), is("sdktest.grf"));
        assertThat(schedule.getRescheduleInMinutes(), is(15));
    }

    @Test(groups = "process", dependsOnMethods = "createSchedule")
    public void executeSchedule() {
        final FutureResult<ScheduleExecution> future = gd.getProcessService().executeSchedule(schedule);
        final ScheduleExecution scheduleExecution = future.get();

        assertThat(scheduleExecution.getStatus(),  is("OK"));
    }

    @Test(groups = "process", dependsOnMethods = "createSchedule")
    public void createTriggeredSchedule() {
        triggeredSchedule = gd.getProcessService().createSchedule(project, new Schedule(process, "sdktest.grf", schedule));

        assertThat(triggeredSchedule, notNullValue());
        assertThat(triggeredSchedule.getExecutable(), is("sdktest.grf"));
        assertThat(triggeredSchedule.getTriggerScheduleId(), is(schedule.getId()));
    }

    @Test(groups = "process", dependsOnMethods = {"createSchedule", "createTriggeredSchedule"})
    public void listSchedules() {
        final PageableList<Schedule> collection = gd.getProcessService().listSchedules(project);

        assertThat(collection, notNullValue());
        assertThat(collection, hasSize(2));
        assertThat(collection.getNextPage(), nullValue());
    }

    @Test(groups = "process", dependsOnMethods = "createSchedule")
    public void updateSchedule() {
        schedule.setState(ScheduleState.DISABLED);
        schedule.setReschedule(Duration.ofMinutes(26));

        schedule = gd.getProcessService().updateSchedule(schedule);

        assertThat(schedule.isEnabled(), is(false));
        assertThat(gd.getProcessService().updateSchedule(schedule).getRescheduleInMinutes(), is(26));
    }

    @Test(groups = "process", dependsOnGroups = "project")
    public void createProcessFromGit() {
        processAppstore = gd.getProcessService().createProcessFromAppstore(project,
                new DataloadProcess("sdktest ruby appstore " + System.getenv("BUILD_NUMBER"), ProcessType.RUBY.toString(),
                "${PUBLIC_APPSTORE}:branch/demo:/test/HelloApp")).get();

        assertThat(processAppstore.getExecutables(), contains("hello.rb"));
    }

    @Test(groups = "process", dependsOnMethods = "createProcessFromGit")
    public void updateProcessFromGit() {
        processAppstore.setPath("${PUBLIC_APPSTORE}:branch/demo:/test/AhojApp");
        processAppstore = gd.getProcessService().updateProcessFromAppstore(processAppstore).get();

        assertThat(processAppstore.getExecutables(), contains("ahoj.rb"));
    }

    public void copy(final String file, final File dir) throws IOException {
        IOUtils.copy(
                new ClassPathResource("/dataload/processes/testgraph/" + file, getClass()).getInputStream(),
                new FileOutputStream(new File(dir, file))
        );
    }

    @Test(groups = "process", dependsOnMethods = "createProcess")
    public void processes() {
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, IsIterableContaining.hasItem(ProcessIdMatcher.hasSameProcessIdAs(process)));
    }

    @Test(groups = "process", dependsOnMethods = "createProcess")
    public void executeProcess() throws Exception {
        ProcessService processService = gd.getProcessService();
        ProcessExecutionDetail executionDetail = processService.executeProcess(new ProcessExecution(process, "sdktest.grf")).get();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        processService.getExecutionLog(executionDetail, outputStream);
        assertThat(outputStream.toString("UTF-8"), allOf(containsString("infoooooooo"), containsString("waaaaaaaarn"),
                containsString("errooooooor"), containsString("fataaaaaaal")));
    }

    @Test(groups = "process", dependsOnMethods = "createProcess",
            expectedExceptions = ProcessExecutionException.class, expectedExceptionsMessageRegExp = "(?s)Can't execute.*")
    public void failExecuteProcess() throws Exception {
        ProcessService processService = gd.getProcessService();
        processService.executeProcess(new ProcessExecution(process, "invalid.grf")).get();
    }

    @Test(dependsOnGroups = "process", dependsOnMethods = "removeSchedule")
    public void removeProcess() throws Exception {
        gd.getProcessService().removeProcess(process);
        gd.getProcessService().removeProcess(processAppstore);
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, Matchers.not(IsIterableContaining.hasItems(ProcessIdMatcher.hasSameProcessIdAs(process), ProcessIdMatcher.hasSameProcessIdAs(processAppstore))));
    }

    @Test(dependsOnGroups = "process")
    public void removeSchedule() {
        gd.getProcessService().removeSchedule(schedule);
        gd.getProcessService().removeSchedule(triggeredSchedule);
        final MultiPageList<Schedule> schedules = (MultiPageList<Schedule>) gd.getProcessService().listSchedules(project);
        assertThat(schedules.collectAll(), Matchers.not(IsIterableContaining.hasItems(ScheduleIdMatcher.hasSameScheduleIdAs(schedule), ScheduleIdMatcher.hasSameScheduleIdAs(triggeredSchedule))));
    }

}
