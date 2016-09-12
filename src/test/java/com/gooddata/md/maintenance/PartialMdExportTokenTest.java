/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class PartialMdExportTokenTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123");
        partialMdExportToken.setUpdateLDMObjects(true);
        partialMdExportToken.setImportAttributeProperties(true);

        assertThat(partialMdExportToken, serializesToJson("/md/maintenance/partialMDImport.json"));
    }
}