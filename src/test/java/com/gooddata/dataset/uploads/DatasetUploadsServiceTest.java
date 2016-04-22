package com.gooddata.dataset.uploads;

import static org.mockito.Mockito.when;

import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;

public class DatasetUploadsServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;

    @Mock
    private RestTemplate restTemplate;

    private DatasetUploadsService datasetUploadsService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        datasetUploadsService = new DatasetUploadsService(restTemplate);
        when(project.getId()).thenReturn(PROJECT_ID);
    }
}