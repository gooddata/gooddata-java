/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import org.testng.annotations.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ReportDefinitionTest {

    public static final String FORMAT = "oneNumber";

    @Test
    public void testDeserialization() throws Exception {
        final ReportDefinition def = readObjectFromResource("/md/report/oneNumberReportDefinition.json", ReportDefinition.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is(FORMAT));
        assertThat(def.getGrid(), is(notNullValue()));
        assertThat(def.getExplainUri(), is("/gdc/md/PROJECT_ID/obj/2274/explain2"));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinition def = new ReportDefinition("Untitled",
                new OneNumberReportDefinitionContent(
                        new Grid(Collections.emptyList(), Collections.emptyList(),
                                Collections.emptyList()), "desc", asList(new Filter("(SELECT [/gdc/md/projectId/obj/123]) >= 2")))
        );
        assertThat(def, jsonEquals(resource("md/report/oneNumberReportDefinition-input.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ReportDefinition def = readObjectFromResource("/md/report/oneNumberReportDefinition.json", ReportDefinition.class);

        assertThat(def.toString(), matchesPattern(ReportDefinition.class.getSimpleName() + "\\[.*\\]"));
    }

}
