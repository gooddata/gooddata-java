/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportDefinitionTest {

    public static final String FORMAT = "oneNumber";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/oneNumberReportDefinition.json");
        final ReportDefinition def = new ObjectMapper().readValue(is, ReportDefinition.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is(FORMAT));
        assertThat(def.getGrid(), is(notNullValue()));
        assertThat(def.getExplainUri(), is("/gdc/md/PROJECT_ID/obj/2274/explain2"));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinition def = new ReportDefinition("Untitled",
                new OneNumberReportDefinitionContent(
                        new Grid(Collections.<String>emptyList(), Collections.<AttributeInGrid>emptyList(),
                                Collections.<GridElement>emptyList()), "desc", asList(new Filter("(SELECT [/gdc/md/projectId/obj/123]) >= 2")))
        );
        assertThat(def, serializesToJson("/md/report/oneNumberReportDefinition-input.json"));
    }

}