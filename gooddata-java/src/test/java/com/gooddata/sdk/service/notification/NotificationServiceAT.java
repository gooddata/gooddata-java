/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.notification;

import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.notification.Channel;
import com.gooddata.sdk.model.notification.EmailConfiguration;
import com.gooddata.sdk.model.notification.MessageTemplate;
import com.gooddata.sdk.model.notification.ProjectEvent;
import com.gooddata.sdk.model.notification.Subscription;
import com.gooddata.sdk.model.notification.TimerEvent;
import com.gooddata.sdk.model.notification.TriggerCondition;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.testng.annotations.Test;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NotificationServiceAT extends AbstractGoodDataAT {

    private Channel channel;
    private Subscription subscription;

    @Test(groups = "notification", dependsOnGroups = "project")
    public void triggerProjectEvent() throws Exception {
        gd.getNotificationService().triggerEvent(project, new ProjectEvent("sdk.event.test", singletonMap("test", "map")));
    }

    @Test(groups = "notification", dependsOnGroups = "project")
    public void createChannel() throws Exception {
        final Account current = gd.getAccountService().getCurrent();

        channel = gd.getNotificationService().createChannel(current,
                new Channel(
                        new EmailConfiguration(current.getEmail()),
                        "test channel"));

        assertThat(channel, is(notNullValue()));
    }

    @Test(groups = "notification", dependsOnMethods = "createChannel")
    public void createSubscription() throws Exception {
        subscription = gd.getNotificationService().createSubscription(project,
                gd.getAccountService().getCurrent(),
                new Subscription(
                        Arrays.asList(new TimerEvent("0 * * * * *"), new TimerEvent("1 * * * * *")),
                        singletonList(channel),
                        new TriggerCondition("true"),
                        new MessageTemplate("test message"),
                        "test subscription"));

        assertThat(subscription, is(notNullValue()));
    }

    @Test(groups = "notification", dependsOnMethods = "createSubscription")
    public void removeChannel() throws Exception {
        gd.getNotificationService().removeChannel(channel);
    }

    @Test(groups = "notification", dependsOnMethods = "removeChannel")
    public void removeSubscription() throws Exception {
        gd.getNotificationService().removeSubscription(subscription);
    }
}