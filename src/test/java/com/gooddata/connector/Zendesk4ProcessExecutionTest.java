/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.gooddata.connector.Zendesk4ProcessExecution.DownloadParams;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class Zendesk4ProcessExecutionTest {

    @Test
    public void testShouldSerialize() {
        assertThat(new Zendesk4ProcessExecution(), jsonEquals(resource("connector/process-execution-empty.json")));
    }

    @Test
    public void testShouldSerializeIncremental() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setIncremental(true);
        assertThat(execution, jsonEquals(resource("connector/process-execution-incremental.json")));
    }

    @Test
    public void testShouldSerializeStartTimes() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setIncremental(true);
        execution.setStartTime("tickets", new DateTime(0L));
        assertThat(execution, jsonEquals(resource("connector/process-execution-zendesk4-startDate.json")));
    }

    @Test
    public void testShouldSerializeDownloadParams() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setDownloadParams(new DownloadParams(true, 5, 3600));
        assertThat(execution, jsonEquals(resource("connector/process-execution-zendesk4-download.json")));
    }

    @Test
    public void testShouldSerializeReload() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setReload(true);
        assertThat(execution, jsonEquals(resource("connector/process-execution-reload.json")));
    }

    @Test
    public void testGetDownloadParams() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        final DownloadParams downloadParams = execution.getDownloadParams();
        assertThat(downloadParams, notNullValue());
    }

    @Test
    public void testToStringFormat() {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();

        assertThat(execution.toString(), matchesPattern(Zendesk4ProcessExecution.class.getSimpleName() + "\\[.*\\]"));
    }
}