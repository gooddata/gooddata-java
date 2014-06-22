/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

import com.gooddata.JsonMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class ReportRequestTest {

    @Test
    public void testSerialization() throws Exception {
        final ReportRequest request = new ReportRequest("DEF-URI");
        assertThat(request, JsonMatchers.serializesToJson("/report/reportRequest.json"));
    }
}