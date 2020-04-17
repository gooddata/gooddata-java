/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportTest {

    private static final String DEFINITION_URI = "/gdc/md/PROJECT_ID/obj/DEF_ID";
    private static final String DOMAIN_URI = "/gdc/md/PROJECT_ID/obj/DOM_ID";

    @Test
    public void testDeserialization() throws Exception {
        final Report report = readObjectFromResource("/md/report/report.json", Report.class);
        assertThat(report, is(notNullValue()));
        assertThat(report.getDefinitions(), is(notNullValue()));
        assertThat(report.getDefinitions(), hasSize(1));
        assertThat(report.getDefinitions().iterator().next(), is(DEFINITION_URI));

        assertThat(report.getDomains(), is(notNullValue()));
        assertThat(report.getDomains(), hasSize(1));
        assertThat(report.getDomains().iterator().next(), is(DOMAIN_URI));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinition definition = mock(ReportDefinition.class);
        when(definition.getUri()).thenReturn(DEFINITION_URI);

        final Report report = new Report("Beers Consumed This Week", definition);
        assertThat(report, jsonEquals(resource("md/report/report-input.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Report report = readObjectFromResource("/md/report/report.json", Report.class);

        assertThat(report.toString(), matchesPattern(Report.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Report report = readObjectFromResource("/md/report/report.json", Report.class);
        final Report deserialized = SerializationUtils.roundtrip(report);

        assertThat(deserialized, jsonEquals(report));
    }
}