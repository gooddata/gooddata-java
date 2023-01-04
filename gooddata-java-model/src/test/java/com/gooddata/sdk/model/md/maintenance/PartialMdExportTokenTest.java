/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.testng.annotations.Test;

public class PartialMdExportTokenTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123");
        partialMdExportToken.setUpdateLDMObjects(true);
        partialMdExportToken.setImportAttributeProperties(true);

        assertThat(partialMdExportToken, jsonEquals(resource("md/maintenance/partialMDImport.json")));
    }

    @Test
    public void testToStringFormat() {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123");

        assertThat(partialMdExportToken.toString(), matchesPattern(PartialMdExportToken.class.getSimpleName() + "\\[.*\\]"));
    }
}