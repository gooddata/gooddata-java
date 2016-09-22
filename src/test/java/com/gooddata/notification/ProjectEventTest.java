/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.notification;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class ProjectEventTest {

    @Test
    public void testSerialization() {
        final ProjectEvent projectEvent = new ProjectEvent("etl.test");
        projectEvent.setParameter("text", "cool");
        assertThat(projectEvent, serializesToJson("/notification/projectEvent.json"));
    }

}