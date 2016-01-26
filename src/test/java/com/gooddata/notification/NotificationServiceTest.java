/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.notification;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.*;

import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class NotificationServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;

    @Mock
    private RestTemplate restTemplate;

    private NotificationService notificationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notificationService = new NotificationService(restTemplate);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    public void testTriggerEvent() throws Exception {
        final ProjectEvent projectEvent = new ProjectEvent("type", singletonMap("key", "value"));
        notificationService.triggerEvent(project, projectEvent);
        verify(restTemplate).postForEntity(eq(ProjectEvent.URI), eq(projectEvent), eq(Void.class), eq(PROJECT_ID));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTriggerNullEvent() throws Exception {
        notificationService.triggerEvent(project, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTriggerNullProject() throws Exception {
        notificationService.triggerEvent(null, mock(ProjectEvent.class));
    }
}