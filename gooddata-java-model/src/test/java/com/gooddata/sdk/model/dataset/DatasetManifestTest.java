/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DatasetManifestTest {

    @Test
    public void testDeserialization() throws Exception {
        final DatasetManifest manifest = readObjectFromResource("/dataset/datasetManifest.json", DatasetManifest.class);

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

        assertThat(manifest, jsonEquals(resource("dataset/datasetManifest-input.json")));
    }

    @Test
    public void shouldSetUploadModeForAllParts() throws Exception {
        final DatasetManifest manifest = new DatasetManifest("dataset", "file.csv", asList(
                new DatasetManifest.Part("FULL", "col1", asList("attr1"), true, null),
                new DatasetManifest.Part("FULL", "col2", asList("attr2"), true, null)
        ));
        manifest.setUploadMode(UploadMode.INCREMENTAL);

        assertThat(manifest.getParts().get(0).getUploadMode(), is("INCREMENTAL"));
        assertThat(manifest.getParts().get(1).getUploadMode(), is("INCREMENTAL"));
    }

    @Test
    public void shouldSetUploadModeDeleteForAllParts() throws Exception {
        final DatasetManifest manifest = new DatasetManifest("dataset", "file.csv", asList(
                new DatasetManifest.Part("FULL", "col1", asList("attr1"), true, null),
                new DatasetManifest.Part("FULL", "col2", asList("attr2"), true, null)
        ));
        manifest.setUploadMode(UploadMode.DELETE_CENTER);

        assertThat(manifest.getParts().get(0).getUploadMode(), is("DELETE-CENTER"));
        assertThat(manifest.getParts().get(1).getUploadMode(), is("DELETE-CENTER"));
    }

    @Test
    public void shouldSetMapping() throws Exception {
        final DatasetManifest manifest = new DatasetManifest("dataset", "file.csv", asList(
                new DatasetManifest.Part("FULL", "col1", asList("attr1"), true, null),
                new DatasetManifest.Part("FULL", "col2", asList("attr2"), true, null)
        ));
        manifest.setMapping("c1", "attr1");
        manifest.setMapping("c2", "attr2");

        assertThat(manifest.getParts().get(0).getColumnName(), is("c1"));
        assertThat(manifest.getParts().get(1).getColumnName(), is("c2"));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailOnMultiPopulates() throws Exception {
        final DatasetManifest manifest = new DatasetManifest("dataset", "file.csv", asList(
                new DatasetManifest.Part("FULL", "col1", asList("attr1", "attr2"), true, null)
        ));
        manifest.setMapping("col", "attr2");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnSetSourceNull() {
        final DatasetManifest manifest = new DatasetManifest("dataset.name");
        manifest.setSource(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnSetFileNull() {
        final DatasetManifest manifest = new DatasetManifest("dataset.name");
        manifest.setFile(null);
    }

    @Test
    public void testToStringFormat() {
        final DatasetManifest manifest = new DatasetManifest("dataset.name");

        assertThat(manifest.toString(), matchesPattern(DatasetManifest.class.getSimpleName() + "\\[.*\\]"));
    }
}
