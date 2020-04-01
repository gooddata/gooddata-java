/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.model.dataload.OutputStage;
import com.gooddata.sdk.model.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OutputStageServiceIT extends AbstractGoodDataIT {

    private static final String OUTPUT_STAGE_ALL_FIELDS = "/dataload/outputStageAllFields.json";
    private static final String OUTPUT_STAGE_NO_PROCESS = "/dataload/outputStageNoProcess.json";
    private static final String OUTPUT_STAGE = "/dataload/outputStage.json";

    private static final String OUTPUT_STAGE_SCHEMA_URI = "/gdc/datawarehouse/instances/instanceId/schemas/default";
    private static final String CLIENT_ID =  "clientId";
    private static final String OUTPUT_STAGE_PREFIX =  "outputStagePrefix";
    private static final String PROJECT_ID = "projectId";

    private OutputStage outputStage;
    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        outputStage = readObjectFromResource(OUTPUT_STAGE_ALL_FIELDS, OutputStage.class);
        project = mock(Project.class);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    public void shouldGetOutputStageByUri() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(outputStage.getUri())
                .respond()
                .withBody(readFromResource(OUTPUT_STAGE_ALL_FIELDS))
                .withStatus(200);

       final OutputStage result = gd.getOutputStageService().getOutputStageByUri(outputStage.getUri());

       assertThat(result, is(notNullValue()));
       assertThat(result.getSchemaUri(), is(equalTo(OUTPUT_STAGE_SCHEMA_URI)));
    }

    @Test
    public void shouldGetOutputStage() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(outputStage.getUri())
                .respond()
                .withBody(readFromResource(OUTPUT_STAGE_ALL_FIELDS))
                .withStatus(200);

        final OutputStage result = gd.getOutputStageService().getOutputStage(project);

        assertThat(result, is(notNullValue()));
        assertThat(result.getSchemaUri(), is(equalTo(OUTPUT_STAGE_SCHEMA_URI)));
    }

    @Test
    public void shouldUpdateOutputStage() throws Exception {
        final OutputStage outputStage = readObjectFromResource(OUTPUT_STAGE, OutputStage.class);

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(outputStage.getUri())
                .havingBody(jsonEquals(readStringFromResource(OUTPUT_STAGE_NO_PROCESS)))
                .respond()
                .withBody(readFromResource(OUTPUT_STAGE_ALL_FIELDS))
                .withStatus(200);

        outputStage.setSchemaUri(OUTPUT_STAGE_SCHEMA_URI);
        outputStage.setClientId(CLIENT_ID);
        outputStage.setOutputStagePrefix(OUTPUT_STAGE_PREFIX);
        OutputStage result = gd.getOutputStageService().updateOutputStage(outputStage);

        assertThat(result.getSchemaUri(), is(equalTo(OUTPUT_STAGE_SCHEMA_URI)));
        assertThat(result.getClientId(), is(equalTo(CLIENT_ID)));
        assertThat(result.getOutputStagePrefix(), is(equalTo(OUTPUT_STAGE_PREFIX)));
    }
}
