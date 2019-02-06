/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;

import org.joda.time.LocalDate;
import org.testng.annotations.Test;

public class CoupaProcessExecutionTest {

    @Test
    public void shouldSerialize() throws Exception {
        final CoupaProcessExecution execution = new CoupaProcessExecution();

        assertThat(execution, jsonEquals(resource("connector/process-execution-empty.json")));
    }

    @Test
    public void shouldSerializeIncremental() throws Exception {
        final CoupaProcessExecution execution = new CoupaProcessExecution();
        execution.setIncremental(true);

        assertThat(execution, jsonEquals(resource("connector/process-execution-incremental.json")));
    }

    @Test
    public void shouldSerializeDownloadDataFrom() throws Exception {
        final CoupaProcessExecution execution = new CoupaProcessExecution();
        execution.setDownloadDataFrom(new LocalDate(2018, 1, 25));

        assertThat(execution, jsonEquals(resource("connector/process-execution-coupa-downloadDate.json")));
    }
}