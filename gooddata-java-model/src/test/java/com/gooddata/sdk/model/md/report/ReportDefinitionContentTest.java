/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import java.util.Collections;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ReportDefinitionContentTest {

    @Test
    public void testDeserialization() throws Exception {
        final ReportDefinitionContent def = readObjectFromResource("/md/report/gridReportDefinitionContent.json", ReportDefinitionContent.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is("grid"));
        assertThat(def.getGrid(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final ReportDefinitionContent def = new GridReportDefinitionContent(
                new Grid(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()));
        assertThat(def, jsonEquals(resource("md/report/gridReportDefinitionContent-input.json")));
    }

    @Test
    public void testToStringFormat() {
        final ReportDefinitionContent def = new GridReportDefinitionContent(
                new Grid(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()));

        assertThat(def.toString(), matchesPattern(GridReportDefinitionContent.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final ReportDefinitionContent def = new GridReportDefinitionContent(
                new Grid(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()));
        final ReportDefinitionContent deserialized = SerializationUtils.roundtrip(def);

        assertThat(deserialized, jsonEquals(def));
    }
}