/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReportDefinitionContentTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/gridReportDefinitionContent.json");
        final ReportDefinitionContent def = new ObjectMapper().readValue(is, ReportDefinitionContent.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is("grid"));
        assertThat(def.getGrid(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinitionContent def = new GridReportDefinitionContent(
                new Grid(Collections.<GridElement>emptyList(), Collections.<GridElement>emptyList(),
                        Collections.<MetricElement>emptyList()));
        assertThat(def, serializesToJson("/md/report/gridReportDefinitionContent-input.json"));
    }

}