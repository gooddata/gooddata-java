/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.connector.ConnectorType.ZENDESK4;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProcessExecutionTest {

    @Test
    public void shouldSerialize() throws Exception {
        final ProcessExecution execution = () -> ZENDESK4;

        assertThat(execution, serializesToJson("/connector/process-execution-empty.json"));
    }
}