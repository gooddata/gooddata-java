/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.md.maintenance.PartialMdExport;
import com.gooddata.md.maintenance.PartialMdExportToken;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Environment;
import com.gooddata.project.Project;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStreamReader;

/**
 * Maintenance acceptance tests.
 */
public class MaintenanceServiceAT extends AbstractGoodDataAT {

    private Project importProject;
    private PartialMdExportToken partialMdExportToken;

    @BeforeClass
    public void setUp() throws Exception {
        final Project newProject = new Project(title + " - metadata import", projectToken);
        newProject.setEnvironment(Environment.TESTING);
        importProject = gd.getProjectService().createProject(newProject).get();
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (importProject != null) {
            gd.getProjectService().removeProject(project);
        }
    }

    @Test(groups = "maintenance", dependsOnMethods = "createMetric")
    public void partialExportMetric() throws Exception {
        partialMdExportToken = gd.getMaintenanceService().partialExport(project, new PartialMdExport(metric.getUri())).get();
        assertThat(partialMdExportToken, notNullValue());
        assertThat(partialMdExportToken.getToken(), not(isEmptyOrNullString()));
        assertThat(partialMdExportToken.isImportAttributeProperties(), is(true));
    }

    @Test(groups = "maintenance", dependsOnMethods = "partialExportMetric")
    public void partialImportDataset() throws Exception {
        final ModelService modelService = gd.getModelService();
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(importProject,
                new InputStreamReader(getClass().getResourceAsStream("/person.json"))).get();
        modelService.updateProjectModel(importProject, projectModelDiff).get();

        gd.getMaintenanceService().partialImport(importProject, partialMdExportToken).get();
        final Metric importedMetric = gd.getMetadataService().getObjById(importProject,
                StringUtils.substringAfterLast(metric.getUri(), "/"), Metric.class);

        assertThat(importedMetric, is(notNullValue()));
    }
}
