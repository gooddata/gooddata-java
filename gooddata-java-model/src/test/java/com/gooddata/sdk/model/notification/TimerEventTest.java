/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.notification;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class TimerEventTest {

    @Test
    public void testDeserialization() throws Exception {
        final TimerEvent timerEvent = readObjectFromResource("/notification/timerEvent.json", TimerEvent.class);

        assertThat(timerEvent.getCronExpression(), is("0 * * * * *"));
    }

    @Test
    public void testSerialization() throws Exception {
        final TimerEvent timerEvent = new TimerEvent("0 * * * * *");

        assertThat(timerEvent, jsonEquals(resource("notification/timerEvent.json")));
    }
}
