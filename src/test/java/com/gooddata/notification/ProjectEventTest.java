/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
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