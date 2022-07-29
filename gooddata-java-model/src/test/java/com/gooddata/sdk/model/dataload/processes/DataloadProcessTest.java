/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DataloadProcessTest {


    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        final DataloadProcess process = readObjectFromResource("/dataload/processes/process.json", DataloadProcess.class);

        assertThat(process, is(notNullValue()));
        assertThat(process.getName(), is("testProcess"));
        assertThat(process.getType(), is("GROOVY"));
        assertThat(process.getExecutables(), hasSize(1));
        assertThat(process.getId(), is("processId"));
        assertThat(process.getUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId"));
        assertThat(process.getExecutionsUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/executions"));
        assertThat(process.getSourceUri(), is("/gdc/projects/PROJECT_ID/dataload/processes/processId/source"));
    }

    @Test
    public void testSerialization() {
        final DataloadProcess process = new DataloadProcess("testProcess", "GROOVY");
        assertThat(process, jsonEquals(resource("dataload/processes/process-input.json")));

        final DataloadProcess processWithPath = new DataloadProcess("testProcess", "GROOVY", "/uploads/process.zip");
        assertThat(processWithPath, jsonEquals(resource("dataload/processes/process-input-withPath.json")));
    }

    @Test
    public void testToStringFormat() {
        final DataloadProcess process = new DataloadProcess("testProcess", "GROOVY");

        assertThat(process.toString(), matchesPattern(DataloadProcess.class.getSimpleName() + "\\[.*\\]"));
    }
}