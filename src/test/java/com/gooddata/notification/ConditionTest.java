/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class ConditionTest {

    @Test
    public void testDeserialization() throws Exception {
        final TriggerCondition condition = readObjectFromResource("/notification/condition.json", TriggerCondition.class);

        assertThat(condition.getExpression(), is("true"));
    }

    @Test
    public void testSerialization() throws Exception {
        final TriggerCondition condition = new TriggerCondition("true");

        assertThat(condition, jsonEquals(resource("notification/condition.json")));
    }
}
