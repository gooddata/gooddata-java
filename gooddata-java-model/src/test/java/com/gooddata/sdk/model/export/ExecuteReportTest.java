/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.export;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExecuteReportTest {

    @Test
    public void testSerialization() throws Exception {
        final ReportRequest request = new ExecuteReport("DEF-URI");
        assertThat(request, jsonEquals(resource("report/executeReport.json")));
    }

    @Test
    public void testToStringFormat() {
        final ReportRequest request = new ExecuteReport("DEF-URI");

        assertThat(request.toString(), matchesPattern(ExecuteReport.class.getSimpleName() + "\\[.*\\]"));
    }
}