/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.model.md.Attribute;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.ScheduledMail;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.project.Project;
import org.junit.jupiter.api.AfterAll;

import java.time.LocalDate;


/**
 * Parent for acceptance tests
 */
public abstract class AbstractGoodDataAT {

    protected static final String title =
            "sdktest " + LocalDate.now() + " " + System.getenv("BUILD_NUMBER");

    protected static final GoodDataEndpoint endpoint = new GoodDataEndpoint(getProperty("host"));

    protected static final GoodData gd =
            new GoodData(
                endpoint.getHostname(),
                getProperty("login"),
                getProperty("password"),
                endpoint.getPort(),
                endpoint.getProtocol(),
                new GoodDataSettings()
            );


    protected static String projectToken;
    protected static Project project;

    protected static String fact;
    protected static Attribute attr;
    protected static Metric metric;
    protected static Report report;
    protected static ReportDefinition reportDefinition;
    protected static ScheduledMail scheduledMail;
    protected static ProjectDashboard dashboard;

    public static String getProperty(String name) {
        final String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("Environment variable " + name + " not found! Available variables: " +
                System.getenv().keySet());
    }

    @AfterAll
    public static void removeProjectAndLogout() {
        if (project != null) {
            gd.getProjectService().removeProject(project);
        }
        gd.logout();
    }
}