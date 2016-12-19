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

public class PartialMdExportTokenTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123");
        partialMdExportToken.setUpdateLDMObjects(true);
        partialMdExportToken.setImportAttributeProperties(true);

        assertThat(partialMdExportToken, serializesToJson("/md/maintenance/partialMDImport.json"));
    }

    @Test
    public void testToStringFormat() {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123");

        assertThat(partialMdExportToken.toString(), matchesPattern(PartialMdExportToken.class.getSimpleName() + "\\[.*\\]"));
    }
}