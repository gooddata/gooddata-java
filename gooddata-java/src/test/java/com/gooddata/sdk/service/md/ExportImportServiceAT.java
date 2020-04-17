/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import static com.gooddata.sdk.model.md.Restriction.identifier;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.md.Entry;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.service.project.model.ModelService;
import com.gooddata.sdk.model.project.model.ModelDiff;
import com.gooddata.sdk.model.project.Environment;
import com.gooddata.sdk.model.project.Project;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Metadata export/import acceptance tests.
 */
public class ExportImportServiceAT extends AbstractGoodDataAT {

    private Project importProject;
    private PartialMdExportToken partialMdExportToken;

    @BeforeClass
    public void setUp() throws Exception {
        final Project newProject = new Project(title + " - metadata import", projectToken);
        newProject.setEnvironment(Environment.TESTING);
        importProject = gd.getProjectService().createProject(newProject).get();

        final ModelService modelService = gd.getModelService();
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(importProject,
                new InputStreamReader(readFromResource("/person.json"))).get();
        modelService.updateProjectModel(importProject, projectModelDiff).get();
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (importProject != null) {
            gd.getProjectService().removeProject(importProject);
        }
    }

    @Test(groups = "exportimport", dependsOnGroups = "md")
    public void partialExportMetric() throws Exception {
        partialMdExportToken = gd.getExportImportService().partialExport(project, new PartialMdExport(metric.getUri())).get();
        assertThat(partialMdExportToken, notNullValue());
        assertThat(partialMdExportToken.getToken(), not(is(emptyOrNullString())));
        assertThat(partialMdExportToken.isImportAttributeProperties(), is(false));
    }

    @Test(groups = "exportimport", dependsOnMethods = "partialExportMetric")
    public void partialImportMetric() throws Exception {
        gd.getExportImportService().partialImport(importProject, partialMdExportToken).get();

        final Collection<Entry> collection = gd.getMetadataService()
                .find(importProject, Metric.class, identifier(metric.getIdentifier()));

        assertThat(collection, hasSize(1));
        final Entry entry = collection.iterator().next();
        assertThat(entry.getTitle(), is(metric.getTitle()));
    }
}
