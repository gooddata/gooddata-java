/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import com.gooddata.GoodDataSettings;
import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;

    private NotificationService notificationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        notificationService = new NotificationService(new RestTemplate(), new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
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