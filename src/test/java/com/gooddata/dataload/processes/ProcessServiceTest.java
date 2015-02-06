package com.gooddata.dataload.processes;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.gooddata.account.AccountService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Random;

public class ProcessServiceTest {

    private static final String PROJECT_ID = "PROJECT_ID";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AccountService accountService;

    @Mock
    private DataStoreService dataStoreService;

    @Mock
    private Project project;

    private ProcessService processService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        processService = new ProcessService(restTemplate, accountService, dataStoreService);
        when(project.getId()).thenReturn(PROJECT_ID);

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

        when(dataStoreService.getUri(anyString())).thenReturn(URI.create("URI"));
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
}