/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.md.MetadataService;
import com.gooddata.md.Metric;
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
        final Metric metric = new Metric("pokus", "SELECT SUM([/gdc/md/nrtk3axzqixzqu3plpq5gnmff5n2k9ob/obj/15089])", "#,##0");
        final Metric m = md.createObj(project, metric);

        final DatasetService datasetService = gd.getDatasetService();
        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "datasetId");
        System.out.println(manifest);
        final ReportDefinition reportDef = md.getObjByUri("/gdc/md/GoodSalesDemoKokot1/obj/1962", ReportDefinition.class);

        final ReportService reportService = gd.getReportService();
        final String imgUri = reportService.exportReport(reportDef, "csv");
        System.out.println(imgUri);

        gd.logout();
    }

}
