/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OutputStageServiceTest {

    private final ObjectMapper MAPPER = new ObjectMapper();

    private static final String OUTPUT_STAGE = "/dataload/outputStage.json";

    @Mock
    private RestTemplate restTemplate;

    private OutputStageService outputStageService;

    private OutputStage outputStage;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        outputStageService = new OutputStageService(restTemplate);
        outputStage = MAPPER.readValue(readFromResource(OUTPUT_STAGE), OutputStage.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetOutputStageByNullUri() throws Exception {
        outputStageService.getOutputStageByUri(null);
    }

    @Test
    public void testGetOutputStageByUri() throws Exception {
        when(restTemplate.getForObject(outputStage.getUri(), OutputStage.class)).thenReturn(outputStage);

        OutputStage outputStageByUri = outputStageService.getOutputStageByUri(outputStage.getUri());

        assertThat(outputStageByUri, is(equalTo(outputStage)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetOutputStageByNullProject() throws Exception {
        outputStageService.getOutputStage(null);
    }

    @Test
    public void testGetOutputStage() throws Exception {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn("projectId");
        when(restTemplate.getForObject(outputStage.getUri(), OutputStage.class)).thenReturn(outputStage);

        OutputStage outputStageByProject = outputStageService.getOutputStage(project);

        assertThat(outputStageByProject, is(equalTo(outputStage)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateOutputStageNullOutputStage() throws Exception {
        outputStageService.updateOutputStage(null);
    }

    @Test
    public void testUpdateOutputStage() throws Exception {
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        when(restTemplate.exchange(eq(outputStage.getUri()), eq(HttpMethod.PUT), any(RequestEntity.class), eq(OutputStage.class))).thenReturn(responseEntity);
        doReturn(outputStage).when(responseEntity).getBody();

        outputStageService.updateOutputStage(outputStage);

        verify(restTemplate, times(1)).exchange(eq(outputStage.getUri()), eq(HttpMethod.PUT), any(RequestEntity.class), eq(OutputStage.class));
    }
}