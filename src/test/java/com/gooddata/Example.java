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
import com.gooddata.model.MaqlDdlTaskStatus;
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

        final Project project = projectService.createProject(new Project("sparkling", "pgroup2")).get();
        System.out.println(project.getSelfLink());

        final ModelService modelService = gd.getModelService();
        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project,
                new InputStreamReader(Example.class.getResourceAsStream("/person.json"))).get();
        final Collection<FutureResult<MaqlDdlTaskStatus>> results = modelService
                .updateProjectModel(project, projectModelDiff);
        for (FutureResult<MaqlDdlTaskStatus> fr : results) {
            final MaqlDdlTaskStatus taskStatus = fr.get();
            System.out.println("MAQL update task success: " + taskStatus.isSuccess());
        }

        final MetadataService md = gd.getMetadataService();

        final String factUri = md.findObjUri(project, Fact.class, Restriction.title("Person Age"));
        final String attrUri = md.findObjUri(project, Attribute.class, Restriction.title("Department"));

        final Metric m = md.createObj(project, new Metric("Age Sum", "SELECT SUM([" + factUri + "])", "#,##0"));

        ReportDefinition definition = GridReportDefinitionContent.create(
                "my report",
                asList("metricGroup"),
                asList(new AttributeInGrid(attrUri)),
                asList(new GridElement(factUri, "Age Sum"))
        );
        definition = md.createObj(project, definition);
        final Report report = md.createObj(project, new Report(definition.getTitle(), definition.getUri(), null));

        final DatasetService datasetService = gd.getDatasetService();
        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, Example.class.getResourceAsStream("/person.csv")).get();

        final ReportDefinition reportDef = md.getObjByUri("/gdc/md/GoodSalesDemoKokot1/obj/1962", ReportDefinition.class);
        final ReportService reportService = gd.getReportService();
        final String imgUri = reportService.exportReport(reportDef, ReportExportFormat.PDF);
        System.out.println(imgUri);

        gd.logout();
    }

}
