/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import org.testng.annotations.Test;

import static com.gooddata.connector.ConnectorType.ZENDESK4;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProcessExecutionTest {

    @Test
    public void shouldSerialize() throws Exception {
        final ProcessExecution execution = () -> ZENDESK4;

        assertThat(execution, jsonEquals(resource("connector/process-execution-empty.json")));
    }
}