/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.testng.annotations.Test;

public class PartialMdExportTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExport partialMdExport =
                new PartialMdExport(true, true, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234");

        assertThat(partialMdExport, serializesToJson("/md/maintenance/partialMDExport.json"));
    }

    @Test
    public void shouldNotSerializeDefaultValues() {
        final PartialMdExport partialMdExport =
                new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234");

        assertThat(partialMdExport, serializesToJson("/md/maintenance/partialMDExport-defaultVals.json"));
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