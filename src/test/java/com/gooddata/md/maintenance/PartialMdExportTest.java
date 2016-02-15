package com.gooddata.md.maintenance;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class PartialMdExportTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/md/maintenance/partialMDExport.json");
        final PartialMdExport partialMdExport = new ObjectMapper().readValue(input, PartialMdExport.class);

        assertThat(partialMdExport.getUris(), containsInAnyOrder("/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234"));
        assertTrue(partialMdExport.isCrossDataCenterExport());
        assertTrue(partialMdExport.isExportAttributeProperties());
    }

    @Test
    public void shouldSerialize() throws Exception {
        final PartialMdExport partialMdExport =
                new PartialMdExport(asList("/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234"), true, true);

        assertThat(partialMdExport, serializesToJson("/md/maintenance/partialMDExport.json"));
    }

    @Test
    public void shouldDeserializeDefaultValues() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/md/maintenance/partialMDExport-defaultVals.json");
        final PartialMdExport partialMdExport = new ObjectMapper().readValue(input, PartialMdExport.class);

        assertThat(partialMdExport.getUris(), containsInAnyOrder("/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234"));
        assertFalse(partialMdExport.isCrossDataCenterExport());
        assertFalse(partialMdExport.isExportAttributeProperties());
    }

    @Test
    public void shouldNotSerializeDefaultValues() {
        final PartialMdExport partialMdExport =
                new PartialMdExport(asList("/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234"), false, false);

        assertThat(partialMdExport, serializesToJson("/md/maintenance/partialMDExport-defaultVals.json"));
    }
}