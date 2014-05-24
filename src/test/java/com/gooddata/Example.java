/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.md.Fact;
import com.gooddata.md.MetadataService;
import com.gooddata.md.Metric;
import com.gooddata.md.Restriction;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import com.gooddata.report.ReportService;

import java.util.Collection;

public class Example {

    public static void main(String... args) {
        final GoodData gd = new GoodData("roman@gooddata.com", "Roman1");

        final AccountService accountService = gd.getAccountService();
        final Account current = accountService.getCurrent();
        System.out.println(current.getId());

        final ProjectService projectService = gd.getProjectService();
        final Collection<Project> projects = projectService.getProjects();
        System.out.println(projects);

        final Project project = projectService.createProject(new Project("sparkling", "pgroup2"));
        System.out.println(project.getLinks().getSelf());

        final MetadataService md = gd.getMetadataService();

        final String factUri = md.findObjUri(project, Fact.class, Restriction.title("myfact"));

        final Metric m = md.createObj(project, new Metric("my sum", "SELECT SUM([" + factUri + "])", "#,##0"));

        final DatasetService datasetService = gd.getDatasetService();
        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, Example.class.getResourceAsStream("/person.csv"));

        final ReportDefinition reportDef = md.getObjByUri("/gdc/md/GoodSalesDemoKokot1/obj/1962", ReportDefinition.class);
        final ReportService reportService = gd.getReportService();
        final String imgUri = reportService.exportReport(reportDef, "csv");
        System.out.println(imgUri);

        gd.logout();
    }

}
