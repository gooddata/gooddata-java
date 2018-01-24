/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import java.util.Collections;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class OneNumberReportDefinitionContentTest {

    @Test
    public void testDeserialization() throws Exception {
        final OneNumberReportDefinitionContent def = (OneNumberReportDefinitionContent)
            readObjectFromResource("/md/report/oneNumberReportDefinitionContent.json", ReportDefinitionContent.class);
        assertThat(def, is(notNullValue()));
        assertThat(def.getFormat(), is("oneNumber"));
        assertThat(def.getGrid(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        final OneNumberReportDefinitionContent def = new OneNumberReportDefinitionContent(
                new Grid(Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList()), "desc", Collections.emptyList()
        );
        assertThat(def, jsonEquals(resource("md/report/oneNumberReportDefinitionContent-input.json")));
    }


    @Test
    public void testSerializable() throws Exception {
        final OneNumberReportDefinitionContent def = (OneNumberReportDefinitionContent)
                readObjectFromResource("/md/report/oneNumberReportDefinitionContent.json", ReportDefinitionContent.class);
        final OneNumberReportDefinitionContent deserialized = SerializationUtils.roundtrip(def);

        assertThat(deserialized, jsonEquals(def));
    }
}