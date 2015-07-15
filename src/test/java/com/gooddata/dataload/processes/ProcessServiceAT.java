package com.gooddata.dataload.processes;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.collections.PageableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import static com.gooddata.dataload.processes.ProcessIdMatcher.hasSameIdAs;
import static java.nio.file.Files.createTempDirectory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Dataload processes acceptance tests.
 */
public class ProcessServiceAT extends AbstractGoodDataAT {

    private DataloadProcess process;

    @Test(groups = "process", dependsOnGroups = "project")
    public void createProcess() throws Exception {
        final File dir = createTempDirectory("sdktest").toFile();
        try {
            copy("sdktest.grf", dir);
            copy("invalid.grf", dir);
            copy("workspace.prm", dir);
            process = gd.getProcessService().createProcess(project, new DataloadProcess(title, ProcessType.GRAPH), dir);
            final Schedule schedule = gd.getProcessService().createSchedule(project, new Schedule(process, "sdktest.grf", "0 0 * * *"));

            assertThat(schedule, notNullValue());
            assertThat(schedule.getExecutable(), is("sdktest.grf"));

            final PageableList<Schedule> collection = gd.getProcessService().listSchedules(project);
            assertThat(collection, notNullValue());
            assertThat(collection, hasSize(1));
            assertThat(collection.getNextPage(), nullValue());

            schedule.setState(ScheduleState.DISABLED);
            assertThat(gd.getProcessService().updateSchedule(project, schedule).isEnabled(), is(false));

            gd.getProcessService().removeSchedule(schedule);
        } finally {
            FileUtils.deleteDirectory(dir);
        }
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
        assertThat(processes, hasItem(hasSameIdAs(process)));
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

    @Test(dependsOnGroups = "process")
    public void removeProcess() throws Exception {
        gd.getProcessService().removeProcess(process);
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, not(hasItem(hasSameIdAs(process))));
    }

}
