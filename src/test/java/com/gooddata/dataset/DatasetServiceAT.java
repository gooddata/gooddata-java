package com.gooddata.dataset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.testng.Assert.fail;

import com.gooddata.AbstractGoodDataAT;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

/**
 * Dataset acceptance tests.
 */
public class DatasetServiceAT extends AbstractGoodDataAT {

    @Test(groups = "dataset", dependsOnGroups = {"md", "datastore"})
    public void loadDataset() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, getClass().getResourceAsStream("/person.csv")).get();
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void loadDatasetBatch() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
        personManifest.setSource(getClass().getResourceAsStream("/person.csv"));
        final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
        cityManifest.setSource(getClass().getResourceAsStream("/city.csv"));

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
            datasetService.loadDataset(project, manifest, getClass().getResourceAsStream("/corruptedPerson.csv")).get();
            fail();
        } catch (DatasetException ex){
            assertThat(ex.getMessage(),is(equalTo("Load datasets [dataset.person] failed: [Number of columns doesn't corespond on line 3 in dataset.person.csv]")));
        }
    }

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void loadDatasetBatchFail() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
        personManifest.setSource(getClass().getResourceAsStream("/corruptedPerson.csv"));
        final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
        cityManifest.setSource(getClass().getResourceAsStream("/city.csv"));

        try {
            datasetService.loadDatasets(project, personManifest, cityManifest).get();
            fail();
        } catch (DatasetException ex){
            assertThat(ex.getMessage(),is(equalTo("Load datasets [dataset.person, dataset.city] failed: [Number of columns doesn't corespond on line 3 in dataset.person.csv]")));
        }
    }
}
