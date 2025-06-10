/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.util.Collections.singletonList;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.gooddata.sdk.model.md.Meta;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SubscriptionTest {

    @Test
    public void testDeserialization() throws Exception {
        final Subscription subscription = readObjectFromResource("/notification/subscription.json", Subscription.class);
        assertThat(subscription.getMeta(), is(notNullValue()));
        assertThat(subscription.getMeta().getUri(), is("/gdc/projects/be2lnpzun7cybpvd7gs1mvtczzr9ijv7/users/876ec68f5630b38de65852ed5d6236ff/subscriptions/59dca8f260b2c601f3c72e1c"));
        assertThat(subscription.getMeta().getTitle(), is("test subscription"));
        assertThat(subscription.getChannels(), is(singletonList("/gdc/account/profile/876ec68f5630b38de65852ed5d6236ff/channelConfigurations/59dca62e60b2c601f3c72e18")));
        assertThat(subscription.getCondition(), is(notNullValue()));
        assertThat(subscription.getCondition().getExpression(), is("true"));
        assertThat(subscription.getTriggers(), hasSize(2));
        assertThat(subscription.getTemplate(), is(notNullValue()));
        assertThat(subscription.getTemplate().getExpression(), is("test message"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Subscription subscription = new Subscription(
                Arrays.asList(new TimerEvent("0 * * * * *"), new TimerEvent("1 * * * * *")),
                new TriggerCondition("true"),
                new MessageTemplate("test message"),
                null,
                singletonList("/gdc/account/profile/876ec68f5630b38de65852ed5d6236ff/channelConfigurations/59dca62e60b2c601f3c72e18"),
                new Meta("test subscription"));

        assertThat(subscription, jsonEquals(resource("notification/subscriptionToCreate.json")));
    }

    @Test
    public void testSerializationWithSubject() {
        Subscription subscription = new Subscription(
                singletonList(new TimerEvent("0 * * * * *")),
                new TriggerCondition("true"),
                new MessageTemplate("test message"),
                new MessageTemplate("test subject"),
                singletonList("/gdc/account/profile/876ec68f5630b38de65852ed5d6236ff/channelConfigurations/59dca62e60b2c601f3c72e18"),
                new Meta("test subscription"));

        assertThat(subscription, jsonEquals(resource("notification/subscriptionWithSubject.json")));
    }

    @Test
    public void testDeserializationWithSubject() {
        Subscription subscription = readObjectFromResource("/notification/subscriptionWithSubject.json", Subscription.class);
        assertThat(subscription.getSubjectTemplate().getExpression(), is("test subject"));
        assertThat(subscription.getTemplate().getExpression(), is("test message"));
        assertThat(subscription.getCondition().getExpression(), is("true"));
        assertThat(subscription.getChannels().get(0), is("/gdc/account/profile/876ec68f5630b38de65852ed5d6236ff/channelConfigurations/59dca62e60b2c601f3c72e18"));
        assertThat(subscription.getMeta().getTitle(), is ("test subscription"));

        Trigger trigger = subscription.getTriggers().get(0);
        assertThat(trigger, instanceOf(TimerEvent.class));
        assertThat(((TimerEvent)trigger).getCronExpression(), is("0 * * * * *"));
    }
}