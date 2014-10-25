/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.connector.ConnectorType.ZENDESK4;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProcessExecutionTest {

    @Test
    public void shouldSerialize() throws Exception {
        final ProcessExecution execution = new ProcessExecution(){
            @Override
            public ConnectorType getConnectorType() {
                return ZENDESK4;
            }
        };

        assertThat(execution, serializesToJson("/connector/process-in.json"));
    }
}