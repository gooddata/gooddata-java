/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatasetManifestTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/datasetManifest.json");
        final DatasetManifest manifest = new ObjectMapper().readValue(stream, DatasetManifest.class);

        assertThat(manifest.getDataSet(), is("dataset.person"));
        assertThat(manifest.getFile(), is("dataset.person.csv"));

        assertThat(manifest.getParts(), hasSize(3));

        final DatasetManifest.Part lastPart = manifest.getParts().get(2);
        assertThat(lastPart.getColumnName(), is("d_person_department.nm_xdepartment"));
        assertThat(lastPart.getUploadMode(), is("FULL"));
        assertThat(lastPart.isReferenceKey(), is(true));
        assertThat(lastPart.getPopulates(), hasSize(1));
        assertThat(lastPart.getPopulates().get(0), is("attr.person.xdepartment"));

        final DatasetManifest.Part datePart = manifest.getParts().get(1);
        assertThat(datePart.getConstraints().get("date"), is("yyyy-MM-dd'T'HH:mm:ssZ"));
    }

    @Test
    public void testSerialization() throws Exception {
        final DatasetManifest.Part part = new DatasetManifest.Part("MODE", "COLUMN", singletonList("POPULATES"), true,
                singletonMap("date", "CONSTRAINT"));
        final DatasetManifest manifest = new DatasetManifest("DATASET", "FILE", singletonList(part));

        assertThat(manifest, serializesToJson("/dataset/datasetManifest-input.json"));
    }
}
