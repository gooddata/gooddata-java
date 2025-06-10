/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import static com.gooddata.sdk.model.md.Restriction.identifier;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.gooddata.sdk.model.md.maintenance.ExportProject;
import com.gooddata.sdk.model.md.maintenance.ExportProjectToken;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.md.Entry;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.service.project.model.ModelService;
import com.gooddata.sdk.model.project.model.ModelDiff;
import com.gooddata.sdk.model.project.Environment;
import com.gooddata.sdk.model.project.Project;
import org.junit.jupiter.api.AfterAll;  
import org.junit.jupiter.api.BeforeAll; 
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Metadata export/import acceptance tests.
 */
public class ExportImportServiceAT extends AbstractGoodDataAT {

    private static Project importProject;
    private static Project emptyProject;
    private PartialMdExportToken partialMdExportToken;
    private ExportProjectToken exportProjectToken;

    @BeforeAll
    public static void setUp() throws Exception {
        final Project newProject = new Project(title + " - metadata import", projectToken);
        newProject.setEnvironment(Environment.TESTING);
        importProject = gd.getProjectService().createProject(newProject).get();
        emptyProject = gd.getProjectService().createProject(newProject).get();

        final ModelService modelService = gd.getModelService();
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(importProject,
                new InputStreamReader(readFromResource("/person.json"))).get();
        modelService.updateProjectModel(importProject, projectModelDiff).get();
    }

    @AfterAll
    public static void tearDown() {
        if (importProject != null) {
            gd.getProjectService().removeProject(importProject);
        }
        if (emptyProject != null) {
            gd.getProjectService().removeProject(emptyProject);
        }
    }

    @Test
    public void partialExportMetric() {
        partialMdExportToken = gd.getExportImportService().partialExport(project, new PartialMdExport(metric.getUri())).get();
        assertThat(partialMdExportToken, notNullValue());
        assertThat(partialMdExportToken.getToken(), not(is(emptyOrNullString())));
        assertThat(partialMdExportToken.isImportAttributeProperties(), is(false));
    }

    @Test
    public void partialImportMetric() {
        gd.getExportImportService().partialImport(importProject, partialMdExportToken).get();

        final Collection<Entry> collection = gd.getMetadataService()
                .find(importProject, Metric.class, identifier(metric.getIdentifier()));

        assertThat(collection, hasSize(1));
        final Entry entry = collection.iterator().next();
        assertThat(entry.getTitle(), is(metric.getTitle()));
    }

    @Test
    public void exportProject() {
        exportProjectToken = gd.getExportImportService().exportProject(project, new ExportProject()).get();
        assertThat(exportProjectToken, notNullValue());
        assertThat(exportProjectToken.getToken(), not(is(emptyOrNullString())));
    }

    @Test
    public void importProject() {
        gd.getExportImportService().importProject(emptyProject, exportProjectToken).get();

        final Collection<Entry> collection = gd.getMetadataService()
                .find(emptyProject, Metric.class, identifier(metric.getIdentifier()));

        assertThat(collection, hasSize(1));
        final Entry entry = collection.iterator().next();
        assertThat(entry.getTitle(), is(metric.getTitle()));
    }
}
