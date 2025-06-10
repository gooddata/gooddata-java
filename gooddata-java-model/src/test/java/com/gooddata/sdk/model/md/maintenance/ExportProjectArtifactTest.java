/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.gooddata.sdk.model.gdc.UriResponse;
import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExportProjectArtifactTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ExportProjectArtifact exportProjectArtifact = readObjectFromResource("/md/maintenance/exportArtifact.json",
                ExportProjectArtifact.class);

        assertThat(exportProjectArtifact.getStatusUri(), is("/gdc/md/projectId/tasks/taskId/status"));
        assertThat(exportProjectArtifact.getToken(), is("TOKEN123"));
    }

    @Test
    public void testToStringFormat() {
        final ExportProjectArtifact exportProjectArtifact = new ExportProjectArtifact(
                new UriResponse("/gdc/md/projectId/tasks/taskId/status"), "TOKEN123");

        assertThat(exportProjectArtifact.toString(), matchesPattern(ExportProjectArtifact.class.getSimpleName() + "\\[.*\\]"));
    }
}