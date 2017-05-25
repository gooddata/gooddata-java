/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

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
        final InputStream stream = getClass().getResourceAsStream("/dataload/outputStageAllFields.json");
        final OutputStage outputStage = new ObjectMapper().readValue(stream, OutputStage.class);

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
        final InputStream stream = getClass().getResourceAsStream("/dataload/outputStage.json");
        final OutputStage outputStage = new ObjectMapper().readValue(stream, OutputStage.class);

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
        final InputStream stream = getClass().getResourceAsStream("/dataload/outputStage.json");
        final OutputStage outputStage = new ObjectMapper().readValue(stream, OutputStage.class);

        outputStage.setSchemaUri(SCHEMA_URI);
        outputStage.setClientId(CLIENT_ID);
        outputStage.setOutputStagePrefix(OUTPUT_STAGE_PREFIX);
        assertThat(outputStage, serializesToJson("/dataload/outputStageNoProcess.json"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataload/outputStage.json");
        final OutputStage outputStage = new ObjectMapper().readValue(stream, OutputStage.class);

        assertThat(outputStage.toString(), matchesPattern(OutputStage.class.getSimpleName() + "\\[.*\\]"));
    }
}
