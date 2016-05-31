/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataload.processes;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static java.net.URI.create;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ProcessServiceTest {

    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String PROCESS_URI = "/gdc/projects/PROJECT_ID/dataload/processes/PROCESS_ID";
    private static final String USER_PROCESS_URI = "/gdc/account/profile/ACCOUNT_ID/dataload/processes";

    @Mock
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
        MockitoAnnotations.initMocks(this);
        processService = new ProcessService(restTemplate, accountService, dataStoreService);
        when(process.getId()).thenReturn(PROCESS_ID);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);
        when(restTemplate.getUriTemplateHandler()).thenReturn(new DefaultUriTemplateHandler());
    }

    @Test
    public void testCreateProcess() throws Exception {

        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        final ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), entityCaptor.capture(), eq(DataloadProcess.class)))
                .thenReturn(new ResponseEntity<>(process, HttpStatus.CREATED));

        processService.createProcess(project, process, createProcessOfSize(1));

        assertNotNull(entityCaptor.getValue());
        assertNotNull(entityCaptor.getValue().getBody());
        assertTrue(entityCaptor.getValue().getBody() instanceof MultiValueMap);

        verifyZeroInteractions(dataStoreService);
    }

    @Test
    public void testCreateProcessLargerThan1MB() throws Exception {

        final DataloadProcess process = new DataloadProcess("test", ProcessType.GRAPH);

        when(dataStoreService.getUri(anyString())).thenReturn(create("URI"));
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), eq(new HttpEntity<>(process)), eq(DataloadProcess.class)))
            .thenReturn(new ResponseEntity<>(process, HttpStatus.CREATED));

        processService.createProcess(project, process, createProcessOfSize(2048));

        verify(dataStoreService).upload(anyString(), notNull(InputStream.class));
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
    public void testUpdateProcessWithNullProject() throws Exception {
        processService.updateProcess(null, process, File.createTempFile("test", null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateProcessWithNullProcess() throws Exception {
        processService.updateProcess(project, null, File.createTempFile("test", null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateProcessWithNullFile() throws Exception {
        processService.updateProcess(project, process, null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testUpdateProcessWithRestClientError() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenThrow(new RestClientException(""));
        processService.updateProcess(project, process, File.createTempFile("test", null));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testUpdateProcessWithNoApiResponse() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenReturn(null);
        processService.updateProcess(project, process, File.createTempFile("test", null));
    }

    @Test
    public void testUpdateProcess() throws Exception {
        when(restTemplate.exchange(eq(create(PROCESS_URI)), any(HttpMethod.class), any(HttpEntity.class),
                eq(DataloadProcess.class))).thenReturn(new ResponseEntity<>(process, HttpStatus.OK));
        final DataloadProcess result = processService
                .updateProcess(project, process, File.createTempFile("test", null));
        assertThat(result, is(process));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProcessByUriWithNullUri() throws Exception {
        processService.getProcessByUri(null);
    }

    @Test
    public void testGetProcessByUri() throws Exception {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenReturn(process);

        final DataloadProcess result = processService.getProcessByUri(PROCESS_URI);
        assertThat(result, is(process));
    }

    @Test(expectedExceptions = ProcessNotFoundException.class)
    public void testGetProcessByUriNotFound() throws Exception {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenThrow(
                new GoodDataRestException(404, "", "", "", ""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetProcessByUriServerError() throws Exception {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class))
                .thenThrow(new GoodDataRestException(500, "", "", "", ""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProcessByUriClientError() throws Exception {
        when(restTemplate.getForObject(PROCESS_URI, DataloadProcess.class)).thenThrow(new RestClientException(""));
        processService.getProcessByUri(PROCESS_URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUserProcessesWithRestClientError() throws Exception {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenThrow(new RestClientException(""));
        processService.listUserProcesses();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUserProcessesWithNullResponse() throws Exception {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class)).thenReturn(null);
        processService.listUserProcesses();
    }

    @Test
    public void testListUserProcessesWithNoProcesses() throws Exception {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenReturn(new DataloadProcesses(Collections.<DataloadProcess>emptyList()));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, empty());
    }

    @Test
    public void testListUserProcessesWithOneProcesses() throws Exception {
        when(restTemplate.getForObject(create(USER_PROCESS_URI), DataloadProcesses.class))
                .thenReturn(new DataloadProcesses(asList(process)));
        final Collection<DataloadProcess> result = processService.listUserProcesses();
        assertThat(result, hasSize(1));
        assertThat(result, contains(process));
    }
}
