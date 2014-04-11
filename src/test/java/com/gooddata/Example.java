/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.md.MetadataService;
import com.gooddata.md.Metric;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;

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

        gd.logout();
    }

}
