/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import com.gooddata.JsonMatchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class Zendesk4ProcessExecutionTest {

    @Test
    public void testShouldSerialize() throws Exception {
        assertThat(new Zendesk4ProcessExecution(), JsonMatchers.serializesToJson(
                "/connector/process-execution-empty.json"));
    }
}