/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.notification;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.notification.*;

import org.junit.jupiter.api.MethodOrderer; 
import org.junit.jupiter.api.Order; 
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceAT extends AbstractGoodDataAT {

    private Channel channel;
    private Subscription subscription;

    @Test
    @Order(1)
    public void triggerProjectEvent() throws Exception {
        gd.getNotificationService().triggerEvent(project, new ProjectEvent("sdk.event.test", singletonMap("test", "map")));
    }

    @Test
    @Order(2)
    public void createChannel() throws Exception {
        final Account current = gd.getAccountService().getCurrent();

        channel = gd.getNotificationService().createChannel(current,
                new Channel(
                        new EmailConfiguration(current.getEmail()),
                        "test channel"));

        assertThat(channel, is(notNullValue()));
    }

    @Test
    @Order(3)
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

    @Test
    @Order(4)
    public void removeChannel() throws Exception {
        gd.getNotificationService().removeChannel(channel);
    }

    @Test
    @Order(5)
    public void removeSubscription() throws Exception {
        gd.getNotificationService().removeSubscription(subscription);
    }
}