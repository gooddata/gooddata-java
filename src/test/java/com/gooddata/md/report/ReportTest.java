/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportTest {

    public static final String DEFINITION = "/gdc/md/PROJECT_ID/obj/DEF_ID";
    public static final String DOMAIN = "/gdc/md/PROJECT_ID/obj/DOM_ID";

    @Test
    public void testDeserialization() throws Exception {
        final Report report = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/report/report.json"), Report.class);
        assertThat(report, is(notNullValue()));
        assertThat(report.getDefinitions(), is(notNullValue()));
        assertThat(report.getDefinitions(), hasSize(1));
        assertThat(report.getDefinitions().iterator().next(), is(DEFINITION));

        assertThat(report.getDomains(), is(notNullValue()));
        assertThat(report.getDomains(), hasSize(1));
        assertThat(report.getDomains().iterator().next(), is(DOMAIN));
    }

    @Test
    public void testSerialization() throws Exception {
        final Report report = new Report("Beers Consumed This Week", DOMAIN, DEFINITION);
        assertThat(report, serializesToJson("/md/report/report-input.json"));
    }

}