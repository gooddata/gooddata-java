/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.notification;

import com.gooddata.sdk.model.notification.Channel;
import com.gooddata.sdk.model.notification.ProjectEvent;
import com.gooddata.sdk.model.notification.Subscription;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";
    private static final String USER_ID = "TEST_USER_ID";

    @Mock
    private Project project;

    @Mock
    private Account account;

    @Mock
    private Subscription subscription;

    @Mock
    private Channel channel;

    private NotificationService notificationService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        notificationService = new NotificationService(new RestTemplate(), new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
        when(account.getId()).thenReturn(USER_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTriggerNullEvent() throws Exception {
        notificationService.triggerEvent(project, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTriggerNullProject() throws Exception {
        notificationService.triggerEvent(null, mock(ProjectEvent.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateChannelNullAccount() throws Exception {
        notificationService.createChannel(null, channel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateChannelNullChannel() throws Exception {
        notificationService.createChannel(mock(Account.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateChannelEmptyAccountId() throws Exception {
        notificationService.createChannel(mock(Account.class), channel);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateSubscriptionNullProject() throws Exception {
        notificationService.createSubscription(null, account, subscription);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateSubscriptionNullAccount() throws Exception {
        notificationService.createSubscription(project, null, subscription);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateSubscriptionNullSubscription() throws Exception {
        notificationService.createSubscription(project, account, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateSubscriptionEmptyProjectId() throws Exception {
        notificationService.createSubscription(mock(Project.class), account, subscription);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateSubscriptionEmptyAccountId() throws Exception {
        notificationService.createSubscription(project, mock(Account.class), subscription);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRemoveChannelNullChannel() throws Exception {
        notificationService.removeChannel(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRemoveChannelEmptyUri() throws Exception {
        notificationService.removeChannel(mock(Channel.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRemoveSubscriptionNullSubscription() throws Exception {
        notificationService.removeSubscription(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRemoveSubscriptionEmptyUri() throws Exception {
        notificationService.removeSubscription(mock(Subscription.class));
    }
}