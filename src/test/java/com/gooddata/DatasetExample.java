package com.gooddata;

import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.project.Project;

/**
 * TODO
 */
public class DatasetExample {

    public static void main(String... args) {
        final GoodData gd = new GoodData("staging.getgooddata.com", "jiri.mikulasek@gooddata.com", "jindrisska");

        final Project project = gd.getProjectService().getProjectById("amxhoeyj7oskijld63tajq0o9f4nhxy7");
        DatasetService datasetService = gd.getDatasetService();
        DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");

        datasetService.loadDataset(project, DatasetExample.class.getResourceAsStream("/person.csv"), manifest);
    }
}
