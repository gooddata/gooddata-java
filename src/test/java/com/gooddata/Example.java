/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.md.Attribute;
import com.gooddata.md.Fact;
import com.gooddata.md.MetadataService;
import com.gooddata.md.Metric;
import com.gooddata.md.Restriction;
import com.gooddata.md.report.AttributeInGrid;
import com.gooddata.md.report.GridElement;
import com.gooddata.md.report.GridReportDefinitionContent;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import com.gooddata.report.ReportExportFormat;
import com.gooddata.report.ReportService;

import java.io.InputStreamReader;
import java.util.Collection;

import static java.util.Arrays.asList;

public class Example {

    public static void main(String... args) {
        final GoodData gd = new GoodData("roman@gooddata.com", "Roman1");

        final AccountService accountService = gd.getAccountService();
        final Account current = accountService.getCurrent();
        System.out.println(current.getId());

        final ProjectService projectService = gd.getProjectService();
        final Collection<Project> projects = projectService.getProjects();
        System.out.println(projects);

        final Project project = projectService.createProject(new Project("sparkling3", "pgroup2")).get();
        System.out.println(project.getSelfLink());

        final ModelService modelService = gd.getModelService();
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project,
                new InputStreamReader(Example.class.getResourceAsStream("/person.json"))).get();
        modelService.updateProjectModel(project, projectModelDiff);

        final MetadataService md = gd.getMetadataService();

        final String factUri = md.findObjUri(project, Fact.class, Restriction.title("Person Age"));
        final String attrUri = md.findObjUri(project, Attribute.class, Restriction.title("Department"));
        final Attribute attr = md.getObjByUri(attrUri, Attribute.class);

        final Metric m = md.createObj(project, new Metric("Age Sum", "SELECT SUM([" + factUri + "])", "#,##0"));

        ReportDefinition definition = GridReportDefinitionContent.create(
                "my report",
                asList("metricGroup"),
                asList(new AttributeInGrid(attr.getDisplayForms().iterator().next().getUri())),
                asList(new GridElement(m.getUri(), "Age Sum"))
        );
        definition = md.createObj(project, definition);
        final Report report = md.createObj(project, new Report(definition.getTitle(), definition.getUri(), null));

        final DatasetService datasetService = gd.getDatasetService();
        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, Example.class.getResourceAsStream("/person.csv")).get();

        final ReportService reportService = gd.getReportService();
        final String exportUri = reportService.exportReport(definition, ReportExportFormat.CSV);
        System.out.println(exportUri);

        gd.logout();
    }

}
