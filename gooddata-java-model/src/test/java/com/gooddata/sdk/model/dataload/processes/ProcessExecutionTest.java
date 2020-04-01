/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProcessExecutionTest {

    @Test
    public void testSerialization() throws Exception {
        final Map<String,String> params = new HashMap<>();
        final Map<String,String> hidden = new HashMap<>();
        params.put("PARAM1", "VALUE1");
        params.put("PARAM2", "VALUE2");
        hidden.put("HIDDEN_PARAM1", "SENSITIVE_VALUE1");
        hidden.put("HIDDEN_PARAM2", "SENSITIVE_VALUE2");

        final DataloadProcess process = readObjectFromResource("/dataload/processes/process.json", DataloadProcess.class);

        final ProcessExecution execution = new ProcessExecution(process, "test.groovy", params, hidden);
        assertThat(execution, jsonEquals(resource("dataload/processes/execution.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final DataloadProcess process = readObjectFromResource("/dataload/processes/process.json", DataloadProcess.class);
        final ProcessExecution execution = new ProcessExecution(process, "test.groovy");

        assertThat(execution.toString(), matchesPattern(ProcessExecution.class.getSimpleName() + "\\[.*\\]"));
    }

}