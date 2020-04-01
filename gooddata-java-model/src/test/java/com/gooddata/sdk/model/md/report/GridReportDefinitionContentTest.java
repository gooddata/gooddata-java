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
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GridReportDefinitionContentTest {

    @Test
    public void testDeserialization() throws Exception {
        final GridReportDefinitionContent def = (GridReportDefinitionContent)
                readObjectFromResource("/md/report/gridReportDefinitionContent.json", ReportDefinitionContent.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is("grid"));
        assertThat(def.getGrid(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final GridReportDefinitionContent def = new GridReportDefinitionContent(
                new Grid(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()));
        assertThat(def, jsonEquals(resource("md/report/gridReportDefinitionContent-input.json")));
    }

    @Test
    public void testSerializable() throws Exception {
        final GridReportDefinitionContent def = (GridReportDefinitionContent)
                readObjectFromResource("/md/report/gridReportDefinitionContent.json", ReportDefinitionContent.class);
        final GridReportDefinitionContent deserialized = SerializationUtils.roundtrip(def);

        assertThat(deserialized, jsonEquals(def));
    }
}