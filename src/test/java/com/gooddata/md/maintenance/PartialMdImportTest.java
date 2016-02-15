package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class PartialMdImportTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/md/maintenance/partialMDImport.json");
        final PartialMdImport partialMdImport = new ObjectMapper().readValue(input, PartialMdImport.class);

        assertThat(partialMdImport.getToken(), is("TOKEN123"));
        assertTrue(partialMdImport.isOverwriteNewer());
        assertTrue(partialMdImport.isUpdateLDMObjects());
        assertTrue(partialMdImport.isImportAttributeProperties());
    }

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdImport partialMdImport = new PartialMdImport("TOKEN123", true, true, true);

        assertThat(partialMdImport, serializesToJson("/md/maintenance/partialMDImport.json"));
    }
}