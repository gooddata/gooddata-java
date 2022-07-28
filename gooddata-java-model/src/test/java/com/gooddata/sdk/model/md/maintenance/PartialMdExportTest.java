/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.testng.annotations.Test;

public class PartialMdExportTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExport partialMdExport =
                new PartialMdExport(true, true, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234");

        assertThat(partialMdExport, jsonEquals(resource("md/maintenance/partialMDExport.json")));
    }

    @Test
    public void shouldNotSerializeDefaultValues() {
        final PartialMdExport partialMdExport =
                new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234");

        assertThat(partialMdExport, jsonEquals(resource("md/maintenance/partialMDExport-defaultVals.json")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyUris() throws Exception {
        new PartialMdExport();
    }

    @Test
    public void testToStringFormat() {
        final PartialMdExport partialMdExport =
                new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234");

        assertThat(partialMdExport.toString(), matchesPattern(PartialMdExport.class.getSimpleName() + "\\[.*\\]"));
    }
}