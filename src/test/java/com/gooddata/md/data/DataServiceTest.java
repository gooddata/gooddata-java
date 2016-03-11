package com.gooddata.md.data;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;

public class DataServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;

    @Mock
    private RestTemplate restTemplate;

    private DataService dataService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dataService = new DataService(restTemplate);
        when(project.getId()).thenReturn(PROJECT_ID);
    }
}