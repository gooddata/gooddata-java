/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.report;

import com.gooddata.JsonMatchers;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExecuteReportTest {

    @Test
    public void testSerialization() throws Exception {
        final ReportRequest request = new ExecuteReport("DEF-URI");
        assertThat(request, JsonMatchers.serializesToJson("/report/executeReport.json"));
    }

    @Test
    public void testToStringFormat() {
        final ReportRequest request = new ExecuteReport("DEF-URI");

        assertThat(request.toString(), matchesPattern(ExecuteReport.class.getSimpleName() + "\\[.*\\]"));
    }
}