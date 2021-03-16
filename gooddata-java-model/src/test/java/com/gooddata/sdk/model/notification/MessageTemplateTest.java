/*
 * (C) 2021 GoodData Corporation.
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

public class MessageTemplateTest {

    @Test
    public void testDeserialization() throws Exception {
        final MessageTemplate template = readObjectFromResource("/notification/template.json", MessageTemplate.class);

        assertThat(template.getExpression(), is("test message"));
    }

    @Test
    public void testSerialization() throws Exception {
        final MessageTemplate template = new MessageTemplate("test message");

        assertThat(template, jsonEquals(resource("notification/template.json")));
    }
}
