/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload.processes;

import com.gooddata.sdk.model.dataload.processes.*;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.model.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Random;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static java.lang.String.format;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessServiceTest {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String SCHEDULE_EXECUTIONS_URI = "/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID/executions";
    private static final String SCHEDULE_EXECUTION_URI = "/gdc/projects/PROJECT_ID/schedules/SCHEDULE_ID/executions/EXECUTION_ID";
    private static final String PROCESS_URI = "/gdc/projects/PROJECT_ID/dataload/processes/PROCESS_ID";
    private static final String USER_PROCESS_URI = "/gdc/account/profile/ACCOUNT_ID/dataload/processes";
    private static final String PROCESS_JSON = format("{\"process\":{\"name\":\"testName\",\"type\":\"testType\",\"links\":{\"self\":\"%s\"}}}", PROCESS_URI);
    private static final String PROCESSES_JSON = format("{\"processes\":{\"items\":[%s]}}", PROCESS_JSON);

    private DataloadProcess process;

    @Mock
    private WebClient webClient;

    @Mock
    private AccountService accountService;
    @Mock
    private Account account;
    @Mock
    private DataStoreService dataStoreService;
    @Mock
    private Project project;

    private ProcessService processService;

    // WebClient mocks 
    private WebClient.RequestBodyUriSpec postSpecMock;
    private WebClient.RequestBodyUriSpec putSpecMock; 
    private WebClient.RequestBodySpec bodySpecMock;
    @SuppressWarnings({"rawtypes", "unchecked"})
    private WebClient.RequestHeadersSpec headersSpecMock;
    private WebClient.RequestHeadersUriSpec uriSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach 
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        processService = new ProcessService(webClient, accountService, dataStoreService, new GoodDataSettings());

        process = OBJECT_MAPPER.readValue(PROCESS_JSON, DataloadProcess.class);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);

        postSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        putSpecMock = mock(WebClient.RequestBodyUriSpec.class); 
        bodySpecMock = mock(WebClient.RequestBodySpec.class);   
        headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);  
        responseSpecMock = mock(WebClient.ResponseSpec.class);      

        // PUT setup
        when(webClient.put()).thenReturn(putSpecMock);
        when(putSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);

        // POST setup
        when(webClient.post()).thenReturn(postSpecMock);
        when(postSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);

        // Common setup
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
    }

    @Test
    public void shouldDeploySmallProcessUsingAPI() throws Exception {
        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        when(webClient.post()).thenReturn(postSpecMock);
        when(postSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(DataloadProcess.class))).thenReturn(Mono.just(process));

        processService.createProcess(project, process, createProcessOfSize(1));

        verifyNoInteractions(dataStoreService);
    }

    @Test
    public void shouldDeployLargeProcessUsingWebDAV() throws Exception {
        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        when(dataStoreService.getUri(anyString())).thenReturn(create("URI"));
        when(webClient.post()).thenReturn(postSpecMock);
        when(postSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);
        when(bodySpecMock.bodyValue(any())).thenReturn(headersSpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(DataloadProcess.class))).thenReturn(Mono.just(process));

        processService.createProcess(project, process, createProcessOfSize(2048));

        verify(dataStoreService).upload(anyString(), notNull());
    }

    private static File createProcessOfSize(int size) throws Exception {
        final Random r = new Random();
        final File file = File.createTempFile("process", ".txt");
        try (FileOutputStream s = new FileOutputStream(file)) {
            for (int i = 0; i < size; i++) {
                final byte[] b = new byte[1024];
                r.nextBytes(b);
                s.write(b);
            }
        }
        file.deleteOnExit();
        return file;
    }

    @Test
    public void testUpdateProcessWithNullProcess() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
            processService.updateProcess(null, File.createTempFile("test", null))
        );
    }

    @Test
    public void testUpdateProcessWithRestClientError() throws Exception {
        when(webClient.put()).thenReturn(putSpecMock);
        when(putSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(DataloadProcess.class)))
            .thenReturn(Mono.error(new RuntimeException("WebClient error")));

        assertThrows(GoodDataException.class, () ->
            processService.updateProcess(process, File.createTempFile("test", null))
        );
    }

    @Test
    public void testUpdateProcessWithNoApiResponse() throws Exception {
        when(webClient.put()).thenReturn(putSpecMock);
        when(putSpecMock.uri(eq(create(PROCESS_URI)))).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(DataloadProcess.class))).thenReturn(Mono.empty());

        assertThrows(GoodDataException.class, () ->
            processService.updateProcess(process, File.createTempFile("test", null))
        );
    }

    @Test
    public void testUpdateProcess() throws Exception {
        when(webClient.put()).thenReturn(putSpecMock);
        when(putSpecMock.uri(any(URI.class))).thenReturn(bodySpecMock);
        when(bodySpecMock.contentType(any())).thenReturn(bodySpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(eq(DataloadProcess.class))).thenReturn(Mono.just(process));
        final DataloadProcess result = processService
            .updateProcess(process, File.createTempFile("test", null));
        assertThat(result, is(process));
    }

    @Test
    public void testGetProcessByUriWithNullUri() {
        assertThrows(IllegalArgumentException.class, () -> processService.getProcessByUri(null));
    }

    @Test
    public void testGetProcessByUri() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(PROCESS_URI)).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcess.class)).thenReturn(Mono.just(process));
        final DataloadProcess result = processService.getProcessByUri(PROCESS_URI);
        assertThat(result, is(process));
    }

    @Test
    public void testGetProcessByUriNotFound() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(PROCESS_URI)).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcess.class))
            .thenReturn(Mono.error(new GoodDataRestException(404, "", "", "", "")));
        assertThrows(ProcessNotFoundException.class, () ->
            processService.getProcessByUri(PROCESS_URI)
        );
    }

    @Test
    public void testGetProcessByUriServerError() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(PROCESS_URI)).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        GoodDataRestException restEx = new GoodDataRestException(500, "", "", "", "");
        when(responseSpecMock.bodyToMono(DataloadProcess.class))
            .thenReturn(Mono.error(restEx));

        GoodDataException ex = assertThrows(GoodDataException.class, () ->
            processService.getProcessByUri(PROCESS_URI)
        );
        assertTrue(ex.getCause() instanceof GoodDataRestException);
        assertEquals(restEx, ex.getCause());
    }

    @Test
    public void testGetProcessByUriClientError() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(PROCESS_URI)).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcess.class))
            .thenReturn(Mono.error(new RuntimeException("WebClient error")));
        assertThrows(GoodDataException.class, () ->
            processService.getProcessByUri(PROCESS_URI)
        );
    }

    @Test
    public void testListUserProcessesWithRestClientError() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(create(USER_PROCESS_URI))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcesses.class))
            .thenReturn(Mono.error(new RuntimeException("WebClient error")));
        assertThrows(GoodDataException.class, () -> processService.listUserProcesses());
    }

    @Test
    public void testListUserProcessesWithNullResponse() {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(create(USER_PROCESS_URI))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcesses.class)).thenReturn(Mono.empty());
        assertThrows(GoodDataException.class, () -> processService.listUserProcesses());
    }

    @Test
    public void testListUserProcessesWithNoProcesses() throws Exception {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(create(USER_PROCESS_URI))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcesses.class))
            .thenReturn(Mono.just(OBJECT_MAPPER.readValue("{\"processes\":{\"items\":[]}}", DataloadProcesses.class)));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, empty());
    }

    @Test
    public void testListUserProcessesWithOneProcesses() throws IOException {
        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(create(USER_PROCESS_URI))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(DataloadProcesses.class))
            .thenReturn(Mono.just(OBJECT_MAPPER.readValue(PROCESSES_JSON, DataloadProcesses.class)));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), is(process.getName()));
        assertThat(result.iterator().next().getType(), is(process.getType()));
    }

    @Test
    public void testExecuteScheduleException() {
        when(webClient.post()).thenReturn(postSpecMock);
        when(postSpecMock.uri(eq(SCHEDULE_EXECUTIONS_URI))).thenReturn(bodySpecMock);
        when(bodySpecMock.bodyValue(any())).thenReturn(headersSpecMock);
        when(bodySpecMock.body(any())).thenAnswer(inv -> headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ScheduleExecution.class))
            .thenReturn(Mono.error(new RuntimeException("WebClient error")));

        final Schedule schedule = mock(Schedule.class);
        when(schedule.getExecutionsUri()).thenReturn(SCHEDULE_EXECUTIONS_URI);

        assertThrows(ScheduleExecutionException.class, () ->
            processService.executeSchedule(schedule)
        );
    }

    @Test
    public void testExecuteScheduleExceptionDuringPolling() {

        final ScheduleExecution execution = mock(ScheduleExecution.class);
        when(execution.getUri()).thenReturn(SCHEDULE_EXECUTION_URI);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.getExecutionsUri()).thenReturn(SCHEDULE_EXECUTIONS_URI);

        when(webClient.post()).thenReturn(postSpecMock);
        when(postSpecMock.uri(eq(SCHEDULE_EXECUTIONS_URI))).thenReturn(bodySpecMock);
        when(bodySpecMock.bodyValue(any())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ScheduleExecution.class))
            .thenReturn(Mono.just(execution));

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri(eq(SCHEDULE_EXECUTION_URI))).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ScheduleExecution.class))
            .thenReturn(Mono.error(new GoodDataRestException(500, "", "", "", "")));

        assertThrows(ScheduleExecutionException.class, () -> {
            FutureResult<ScheduleExecution> futureResult = processService.executeSchedule(schedule);
            futureResult.get();
        });
    }

}
