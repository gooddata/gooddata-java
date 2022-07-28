/*
 * (C) 2022 GoodData Corporation.
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

public class EmailConfigurationTest {

    @Test
    public void testDeserialization() throws Exception {
        final EmailConfiguration emailConfiguration = readObjectFromResource("/notification/emailConfiguration.json", EmailConfiguration.class);

        assertThat(emailConfiguration.getTo(), is("fake@gooddata.com"));
    }

    @Test
    public void testSerialization() throws Exception {
        final EmailConfiguration emailConfiguration = new EmailConfiguration("fake@gooddata.com");

        assertThat(emailConfiguration, jsonEquals(resource("notification/emailConfiguration.json")));
    }

}
