/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.account.Account;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

public class NotificationServiceIT extends AbstractGoodDataIT {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String PROJECT_EVENT_PATH = ProjectEvent.TEMPLATE.expand(PROJECT_ID).toString();

    private Project project;
    private Account account;
    private Channel channel;
    private Subscription subscription;

    @BeforeClass
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
        account = readObjectFromResource("/account/account.json", Account.class);
        channel = readObjectFromResource("/notification/channel.json", Channel.class);
        subscription = readObjectFromResource("/notification/subscription.json", Subscription.class);
    }

    @Test
    public void shouldTriggerEvent() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PROJECT_EVENT_PATH)
            .respond()
                .withStatus(204);

        gd.getNotificationService().triggerEvent(project, new ProjectEvent("type", singletonMap("key", "value")));
    }

    @Test
    public void shouldCreateChannel() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Channel.URI_TEMPLATE.expand(account.getId()).toString())
                .havingBody(jsonEquals(readStringFromResource("/notification/channelToCreate.json")))
             .respond()
                .withStatus(201)
                .withBody(readFromResource("/notification/channel.json"));

        final Channel channel = gd.getNotificationService().createChannel(account,
                new Channel(
                        new EmailConfiguration(account.getEmail()),
                        "test channel"));

        assertThat(channel, is(notNullValue()));
    }

    @Test
    public void shouldCreateSubscription() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Subscription.URI_TEMPLATE.expand(project.getId(), account.getId()).toString())
                .havingBody(jsonEquals(readStringFromResource("/notification/subscriptionToCreate.json")))
            .respond()
                .withStatus(201)
                .withBody(readFromResource("/notification/subscription.json"));

        final Subscription subscription = gd.getNotificationService().createSubscription(project,
                account,
                new Subscription(
                        Arrays.asList(new TimerEvent("0 * * * * *"), new TimerEvent("1 * * * * *")),
                        singletonList(channel),
                        new TriggerCondition("true"),
                        new MessageTemplate("test message"),
                        "test subscription"));

        assertThat(subscription, is(notNullValue()));
    }

    @Test
    public void shouldRemoveChannel() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(channel.getMeta().getUri())
            .respond()
                .withStatus(204);

        gd.getNotificationService().removeChannel(channel);
    }

    @Test
    public void shouldRemoveSubscription() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(subscription.getMeta().getUri())
            .respond()
                .withStatus(204);

        gd.getNotificationService().removeSubscription(subscription);
    }
}