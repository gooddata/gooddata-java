/*
 * (C) 2022 GoodData Corporation.
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
import static org.springframework.http.HttpMethod.GET;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
    private RestTemplate restTemplate;
    @Mock
    private AccountService accountService;
    @Mock
    private Account account;
    @Mock
    private DataStoreService dataStoreService;
    @Mock
    private Project project;

    private ProcessService processService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        processService = new ProcessService(restTemplate, accountService, dataStoreService, new GoodDataSettings());
        process = OBJECT_MAPPER.readValue(PROCESS_JSON, DataloadProcess.class);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);
    }

    @Test
    public void shouldDeploySmallProcessUsingAPI() throws Exception {

        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        final ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), entityCaptor.capture(), eq(DataloadProcess.class)))
                .thenReturn(new ResponseEntity<>(process, HttpStatus.CREATED));

        processService.createProcess(project, process, createProcessOfSize(1));

        assertNotNull(entityCaptor.getValue());
        assertNotNull(entityCaptor.getValue().getBody());
        assertTrue(entityCaptor.getValue().getBody() instanceof MultiValueMap);

        verifyNoInteractions(dataStoreService);
    }

    @Test
    public void shouldDeployLargeProcessUsingWebDAV() throws Exception {

        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        when(dataStoreService.getUri(anyString())).thenReturn(create("URI"));
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), eq(new HttpEntity<>(process)), eq(DataloadProcess.class)))
            .thenReturn(new ResponseEntity<>(process, HttpStatus.CREATED));

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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateProcessWithNullProcess() throws Exception {
        processService.updateProcess(null, File.createTempFile("test", null));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testUpdateProcessWithRestClientError() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenThrow(new RestClientException(""));
        processService.updateProcess(process, File.createTempFile("test", null));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testUpdateProcessWithNoApiResponse() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenReturn(null);
        processService.updateProcess(process, File.createTempFile("test", null));
    }

    @Test
    public void testUpdateProcess() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenReturn(new ResponseEntity<>(process, HttpStatus.OK));
        final DataloadProcess result = processService
                .updateProcess(process, File.createTempFile("test", null));
        assertThat(result, is(process));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProcessByUriWithNullUri() {
        processService.getProcessByUri(null);
    }

    @Test
    public void testGetProcessByUri() {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenReturn(process);

        final DataloadProcess result = processService.getProcessByUri(PROCESS_URI);
        assertThat(result, is(process));
    }

    @Test(expectedExceptions = ProcessNotFoundException.class)
    public void testGetProcessByUriNotFound() {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenThrow(
                new GoodDataRestException(404, "", "", "", ""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetProcessByUriServerError() {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class))
                .thenThrow(new GoodDataRestException(500, "", "", "", ""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProcessByUriClientError() {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenThrow(new RestClientException(""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUserProcessesWithRestClientError() {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenThrow(new RestClientException(""));
        processService.listUserProcesses();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUserProcessesWithNullResponse() {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class)).thenReturn(null);
        processService.listUserProcesses();
    }

    @Test
    public void testListUserProcessesWithNoProcesses() throws Exception {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenReturn(OBJECT_MAPPER.readValue("{\"processes\":{\"items\":[]}}", DataloadProcesses.class));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, empty());
    }

    @Test
    public void testListUserProcessesWithOneProcesses() throws IOException {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenReturn(OBJECT_MAPPER.readValue(PROCESSES_JSON, DataloadProcesses.class));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), is(process.getName()));
        assertThat(result.iterator().next().getType(), is(process.getType()));
    }

    @Test(expectedExceptions = ScheduleExecutionException.class)
    public void testExecuteScheduleException() {
        when(restTemplate.postForObject(eq(SCHEDULE_EXECUTIONS_URI), any(ScheduleExecution.class), eq(ScheduleExecution.class)))
                .thenThrow(new RestClientException(""));

        final Schedule schedule = mock(Schedule.class);

        when(schedule.getExecutionsUri()).thenReturn(SCHEDULE_EXECUTIONS_URI);

        processService.executeSchedule(schedule);
    }

    @Test(expectedExceptions = ScheduleExecutionException.class)
    public void testExecuteScheduleExceptionDuringPolling() {
        final ScheduleExecution execution = mock(ScheduleExecution.class);
        when(execution.getUri()).thenReturn(SCHEDULE_EXECUTION_URI);

        final Schedule schedule = mock(Schedule.class);
        when(schedule.getExecutionsUri()).thenReturn(SCHEDULE_EXECUTIONS_URI);

        when(restTemplate.postForObject(eq(SCHEDULE_EXECUTIONS_URI), any(ScheduleExecution.class), eq(ScheduleExecution.class)))
                .thenReturn(execution);

        when(restTemplate.execute(eq(URI.create(SCHEDULE_EXECUTION_URI)), eq(GET), any(), any()))
                .thenThrow(mock(GoodDataRestException.class));

        FutureResult<ScheduleExecution> futureResult = processService.executeSchedule(schedule);
        futureResult.get();
    }
}
