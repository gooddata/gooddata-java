/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ReportDefinitionTest {

    public static final String FORMAT = "oneNumber";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/oneNumberReportDefinition.json");
        final ReportDefinition def = new ObjectMapper().readValue(is, ReportDefinition.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is(FORMAT));
        assertThat(def.getGrid(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinition def = new ReportDefinition("Untitled",
                new OneNumberReportDefinitionContent(
                        new Grid(Collections.<String>emptyList(), Collections.<GridElement>emptyList(),
                                Collections.<GridElement>emptyList()), "desc")
        );
        assertThat(def, serializesToJson("/md/report/oneNumberReportDefinition-input.json"));
    }

}