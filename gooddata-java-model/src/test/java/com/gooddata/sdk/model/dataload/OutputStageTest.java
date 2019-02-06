/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class OutputStageTest {

    private static final String SCHEMA_URI = "/gdc/datawarehouse/instances/instanceId/schemas/default";
    private static final String SELF_LINK = "/gdc/dataload/projects/projectId/outputStage";
    private static final String SQL_DIFF =  "/gdc/dataload/projects/projectId/outputStage/sqlDiff";
    private static final String PROCESS_URI = "/gdc/projects/projectId/dataload/processes/processId";
    private static final String CLIENT_ID = "clientId";
    private static final String OUTPUT_STAGE_PREFIX = "outputStagePrefix";

    private static final Map<String, String> LINKS = new LinkedHashMap<String, String>() {{
        put("self", SELF_LINK);
        put("outputStageDiff", SQL_DIFF);
        put("dataloadProcess", PROCESS_URI);
    }};

    @Test
    public void testDeserializationAllFields() throws Exception {
        final OutputStage outputStage = readObjectFromResource("/dataload/outputStageAllFields.json", OutputStage.class);

        assertThat(outputStage.getSchemaUri(), is(equalTo(SCHEMA_URI)));
        assertThat(outputStage.getClientId(), is(equalTo(CLIENT_ID)));
        assertThat(outputStage.getOutputStagePrefix(), is(equalTo(OUTPUT_STAGE_PREFIX)));
        assertThat(outputStage.getLinks(), is(equalTo(LINKS)));
        assertThat(outputStage.getUri(), is(equalTo(SELF_LINK)));
        assertThat(outputStage.getOutputStageDiffUri(), is(equalTo(SQL_DIFF)));
        assertThat(outputStage.getDataloadProcessUri(), is(equalTo(PROCESS_URI)));
        assertThat(outputStage.hasSchemaUri(), is(true));
        assertThat(outputStage.hasClientId(), is(true));
        assertThat(outputStage.hasOutputStagePrefix(), is(true));
    }

    @Test
    public void testDeserialization() throws Exception {
        final OutputStage outputStage = readObjectFromResource("/dataload/outputStage.json", OutputStage.class);

        assertThat(outputStage.getSchemaUri(), is(nullValue()));
        assertThat(outputStage.getClientId(), is(nullValue()));
        assertThat(outputStage.getOutputStagePrefix(), is(nullValue()));
        assertThat(outputStage.getUri(), is(equalTo(SELF_LINK)));
        assertThat(outputStage.getOutputStageDiffUri(), is(equalTo(SQL_DIFF)));
        assertThat(outputStage.getDataloadProcessUri(), is(nullValue()));
        assertThat(outputStage.hasSchemaUri(), is(false));
        assertThat(outputStage.hasClientId(), is(false));
        assertThat(outputStage.hasOutputStagePrefix(), is(false));
    }

    @Test
    public void testSerializationAfterDeserialization() throws Exception {
        final OutputStage outputStage = readObjectFromResource("/dataload/outputStage.json", OutputStage.class);

        outputStage.setSchemaUri(SCHEMA_URI);
        outputStage.setClientId(CLIENT_ID);
        outputStage.setOutputStagePrefix(OUTPUT_STAGE_PREFIX);
        assertThat(outputStage, jsonEquals(resource("dataload/outputStageNoProcess.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final OutputStage outputStage = readObjectFromResource("/dataload/outputStage.json", OutputStage.class);

        assertThat(outputStage.toString(), matchesPattern(OutputStage.class.getSimpleName() + "\\[.*\\]"));
    }
}
