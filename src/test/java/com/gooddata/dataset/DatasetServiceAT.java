/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

/**
 * Dataset acceptance tests.
 */
public class DatasetServiceAT extends AbstractGoodDataAT {

    private static final String FAILED_LOAD_PATTERN = "Number of columns does(n't| not) co[r]{1,2}espond (in dataset.person.csv at line 3|on line 3 in dataset.person.csv)";

    @Test(groups = "dataset", dependsOnGroups = {"md", "datastore"})
    public void loadDataset() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, readFromResource("/person.csv")).get();
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void loadDatasetBatch() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
        personManifest.setSource(readFromResource("/person.csv"));
        final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
        cityManifest.setSource(readFromResource("/city.csv"));

        datasetService.loadDatasets(project, personManifest, cityManifest).get();
    }

    @Test(groups = "dataset", dependsOnMethods = "loadDatasetBatch")
    public void updateData() {
        final DatasetService datasetService = gd.getDatasetService();
        datasetService.updateProjectData(project, "DELETE FROM {attr.person.name} WHERE {label.person.name} = \"not exists\";");
    }

    @Test(groups = "dataset", dependsOnGroups = {"md", "datastore"})
    public void loadDatasetFail(){
        final DatasetService datasetService = gd.getDatasetService();
        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        try {
            datasetService.loadDataset(project, manifest, readFromResource("/corruptedPerson.csv")).get();
            fail();
        } catch (DatasetException ex){
            assertThat(ex.getMessage(), matchesPattern("Load datasets \\[dataset.person\\] failed: \\[" + FAILED_LOAD_PATTERN + "\\]"));
        }
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void loadDatasetBatchFail() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
        personManifest.setSource(readFromResource("/corruptedPerson.csv"));
        final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
        cityManifest.setSource(readFromResource("/city.csv"));

        try {
            datasetService.loadDatasets(project, personManifest, cityManifest).get();
            fail();
        } catch (DatasetException ex){
            assertThat(ex.getMessage(), matchesPattern("Load datasets \\[dataset.person, dataset.city\\] failed: \\[" + FAILED_LOAD_PATTERN + "\\]"));
        }
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void listUploadsForDataset() throws Exception {
        final Collection<Upload> uploads = gd.getDatasetService().listUploadsForDataset(project, "dataset.person");

        assertThat(uploads, notNullValue());
        assertFalse(uploads.isEmpty());
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void getLastUploadForDataset() throws Exception {
        final Upload lastUpload = gd.getDatasetService().getLastUploadForDataset(project, "dataset.person");

        assertThat(lastUpload, notNullValue());
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset", "loadDatasetFail"})
    public void getUploadStatistics() throws Exception {
        final UploadStatistics uploadStatistics = gd.getDatasetService().getUploadStatistics(project);

        assertThat(uploadStatistics, notNullValue());
        assertThat(uploadStatistics.getUploadsCount("OK"), greaterThan(0));
        assertThat(uploadStatistics.getUploadsCount("ERROR"), greaterThan(0));
    }
}
