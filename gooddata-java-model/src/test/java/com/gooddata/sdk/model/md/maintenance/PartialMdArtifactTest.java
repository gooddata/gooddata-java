/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.gooddata.sdk.model.gdc.UriResponse;
import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class PartialMdArtifactTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final PartialMdArtifact partialMdArtifact = readObjectFromResource("/md/maintenance/partialMDArtifact.json", PartialMdArtifact.class);

        assertThat(partialMdArtifact.getStatusUri(), is("/gdc/md/projectId/tasks/taskId/status"));
        assertThat(partialMdArtifact.getToken(), is("TOKEN123"));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdArtifact partialMdArtifact = new PartialMdArtifact(new UriResponse("/gdc/md/projectId/tasks/taskId/status"), "TOKEN123");

        assertThat(partialMdArtifact, jsonEquals(resource("md/maintenance/partialMDArtifact.json")));
    }

    @Test
    public void testToStringFormat() {
        final PartialMdArtifact partialMdArtifact = new PartialMdArtifact(new UriResponse("/gdc/md/projectId/tasks/taskId/status"), "TOKEN123");

        assertThat(partialMdArtifact.toString(), matchesPattern(PartialMdArtifact.class.getSimpleName() + "\\[.*\\]"));
    }
}