package com.gooddata.dataload.processes;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.FutureResult;
import com.gooddata.project.Project;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collection;

import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class ProcessServiceIT extends AbstractGoodDataIT {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String PROCESSES_PATH = DataloadProcesses.TEMPLATE.expand("PROJECT_ID").toString();
    private static final String PROCESS_ID = "processId";
    private static final String PROCESS_PATH = DataloadProcess.TEMPLATE.expand("PROJECT_ID", PROCESS_ID).toString();
    private static final String PROCESS_SOURCE_PATH = PROCESS_PATH + "/source";
    private static final String EXECUTIONS_PATH = PROCESS_PATH + "/executions";
    private static final String EXECUTION_PATH = EXECUTIONS_PATH + "/executionId";
    private static final String EXECUTION_DETAIL_PATH = EXECUTION_PATH + "/detail";

    private Project project;

    private DataloadProcess process;

    private File file;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readResource("/project/project.json"), Project.class);
        process = MAPPER.readValue(readResource("/dataload/processes/process.json"), DataloadProcess.class);
        file = File.createTempFile("test", ".groovy");
    }

    @Test
    public void shouldCreateProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withBody(readResource("/dataload/processes/process.json"))
                .withStatus(201);

        final DataloadProcess process = gd.getProcessService().createProcess(project, new DataloadProcess("testProcess", "GROOVY"), file);
        assertThat(process, notNullValue());
        assertThat(process.getExecutables(), contains("test.groovy"));
    }

    @Test
    public void shouldListProcesses() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROCESSES_PATH)
            .respond()
                .withBody(readResource("/dataload/processes/processes.json"))
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
                .withBody(readResource("/dataload/processes/process.json"))
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
        final ProcessExecutionDetail executionDetail = MAPPER.readValue(readResource("/dataload/processes/executionDetail.json"), ProcessExecutionDetail.class);
        gd.getProcessService().getExecutionLog(executionDetail, outputStream);
        assertThat(outputStream.toString(), is(log));
    }

    @Test
    public void shouldExecuteProcess() throws Exception {
        mockExecutionRequests();
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_DETAIL_PATH)
            .respond()
                .withBody(readResource("/dataload/processes/executionDetail-success.json"))
                .withStatus(200);

        final FutureResult<ProcessExecutionDetail> result = gd.getProcessService().executeProcess(new ProcessExecution(process, "test.groovy"));
        final ProcessExecutionDetail executionDetail = result.get();

        assertJsonEquals(MAPPER.readValue(readResource("/dataload/processes/executionDetail-success.json"), ProcessExecutionDetail.class), executionDetail);
    }

    @Test(expectedExceptions = ProcessExecutionException.class)
    public void shouldThrowOnExecuteProcessError() throws Exception {
        mockExecutionRequests();
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_DETAIL_PATH)
            .respond()
                .withBody(readResource("/dataload/processes/executionDetail.json"))
                .withStatus(200);

        gd.getProcessService().executeProcess(new ProcessExecution(process, "test.groovy")).get();
    }

    private void mockExecutionRequests() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXECUTIONS_PATH)
            .respond()
                .withBody(readResource("/dataload/processes/executionTask.json"))
                .withHeader("Location", EXECUTION_PATH)
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(EXECUTION_PATH)
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(204);
    }
}
