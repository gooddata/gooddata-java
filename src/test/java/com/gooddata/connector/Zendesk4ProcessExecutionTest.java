/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.gooddata.JsonMatchers;
import com.gooddata.connector.Zendesk4ProcessExecution.DownloadParams;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class Zendesk4ProcessExecutionTest {

    @Test
    public void testShouldSerialize() throws Exception {
        assertThat(new Zendesk4ProcessExecution(), JsonMatchers.serializesToJson(
                "/connector/process-execution-empty.json"));
    }

    @Test
    public void testShouldSerializeIncremental() throws Exception {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setIncremental(true);
        assertThat(execution, JsonMatchers.serializesToJson(
                "/connector/process-execution-incremental.json"));
    }

    @Test
    public void testShouldSerializeStartTimes() throws Exception {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setIncremental(true);
        execution.setStartTime("tickets", new DateTime(0L));
        assertThat(execution, JsonMatchers.serializesToJson(
                "/connector/process-execution-startDate.json"));
    }

    @Test
    public void testShouldSerializeDownloadParams() throws Exception {
        final Zendesk4ProcessExecution execution = new Zendesk4ProcessExecution();
        execution.setDownloadParams(new DownloadParams(true, 5, 3600));
        assertThat(execution, JsonMatchers.serializesToJson(
                "/connector/process-execution-download.json"));
    }

    @Test
    public void testGetDownloadParams() throws Exception {
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