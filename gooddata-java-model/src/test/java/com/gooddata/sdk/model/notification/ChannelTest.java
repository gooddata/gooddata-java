/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class ChannelTest {

    @Test
    public void testDeserialization() throws Exception {
        final Channel channel = readObjectFromResource("/notification/channel.json", Channel.class);

        assertThat(channel.getMeta(), is(notNullValue()));
        assertThat(channel.getMeta().getUri(), is("/gdc/account/profile/876ec68f5630b38de65852ed5d6236ff/channelConfigurations/59dca62e60b2c601f3c72e18"));
        assertThat(channel.getMeta().getTitle(), is("test channel"));
        assertThat(channel.getConfiguration(), is(notNullValue()));
        assertThat(channel.getConfiguration(), is(instanceOf(EmailConfiguration.class)));
        assertThat(((EmailConfiguration) channel.getConfiguration()).getTo(), is("fake@gooddata.com"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Channel channel = new Channel(
                new EmailConfiguration("fake@gooddata.com"),
                "test channel");

        assertThat(channel, jsonEquals(resource("notification/channelToCreate.json")));
    }
}
