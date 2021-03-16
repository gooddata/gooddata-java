/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ExportProjectTest {

    @Test
    public void shouldSerialize() {
        final ExportProject exportProject = new ExportProject(false, false, true, true, "test");

        assertThat(exportProject, jsonEquals(resource("md/maintenance/exportProject.json")));
    }

    @Test
    public void shouldSerializeDefaultValues() {
        final ExportProject exportProject = new ExportProject();

        assertThat(exportProject, jsonEquals(resource("md/maintenance/exportProject-defaultVals.json")));
    }

    @Test
    public void testToStringFormat() {
        final ExportProject exportProject = new ExportProject();

        assertThat(exportProject.toString(), matchesPattern(ExportProject.class.getSimpleName() + "\\[.*\\]"));
    }
}