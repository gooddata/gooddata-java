package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class PartialMdExportTokenTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExportToken partialMdExportToken = new PartialMdExportToken("TOKEN123", true, true, true);

        assertThat(partialMdExportToken, serializesToJson("/md/maintenance/partialMDImport.json"));
    }
}