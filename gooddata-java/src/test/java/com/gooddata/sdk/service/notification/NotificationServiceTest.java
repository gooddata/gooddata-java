/*
 * (C) 2023 GoodData Corporation.
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
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.mockito.junit.jupiter.MockitoSettings;
    import org.mockito.quality.Strictness;
    import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertThrows;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.when;

    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
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

        @Mock
        private WebClient webClient;

        private NotificationService notificationService;

        @BeforeEach
        public void setUp() {
            notificationService = new NotificationService(webClient, new GoodDataSettings());
            when(project.getId()).thenReturn(PROJECT_ID);
            when(account.getId()).thenReturn(USER_ID);
        }

        @Test
        void testTriggerNullEvent() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.triggerEvent(project, null));
        }

        @Test
        void testTriggerNullProject() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.triggerEvent(null, mock(ProjectEvent.class)));
        }

        @Test
        void testCreateChannelNullAccount() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createChannel(null, channel));
        }

        @Test
        void testCreateChannelNullChannel() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createChannel(mock(Account.class), null));
        }

        @Test
        void testCreateChannelEmptyAccountId() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createChannel(mock(Account.class), channel));
        }

        @Test
        void testCreateSubscriptionNullProject() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createSubscription(null, account, subscription));
        }

        @Test
        void testCreateSubscriptionNullAccount() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createSubscription(project, null, subscription));
        }

        @Test
        void testCreateSubscriptionNullSubscription() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createSubscription(project, account, null));
        }

        @Test
        void testCreateSubscriptionEmptyProjectId() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createSubscription(mock(Project.class), account, subscription));
        }

        @Test
        void testCreateSubscriptionEmptyAccountId() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.createSubscription(project, mock(Account.class), subscription));
        }

        @Test
        void testRemoveChannelNullChannel() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.removeChannel(null));
        }

        @Test
        void testRemoveChannelEmptyUri() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.removeChannel(mock(Channel.class)));
        }

        @Test
        void testRemoveSubscriptionNullSubscription() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.removeSubscription(null));
        }

        @Test
        void testRemoveSubscriptionEmptyUri() {
            assertThrows(IllegalArgumentException.class, () -> notificationService.removeSubscription(mock(Subscription.class)));
        }
    }
