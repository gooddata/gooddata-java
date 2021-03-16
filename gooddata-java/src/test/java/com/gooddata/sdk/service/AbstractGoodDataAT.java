/*
 * (C) 2021 GoodData Corporation.
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
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.testng.annotations.AfterSuite;

import java.time.LocalDate;

import static com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider.createHttpClient;

/**
 * Parent for acceptance tests
 */
public abstract class AbstractGoodDataAT {

    protected static final String title =
            "sdktest " + LocalDate.now() + " " + System.getenv("BUILD_NUMBER");

    protected static final GoodDataEndpoint endpoint = new GoodDataEndpoint(getProperty("host"));

    protected static final GoodData gd =
            new GoodData(
                    new SingleEndpointGoodDataRestProvider(endpoint, new GoodDataSettings(), (b, e, s) -> {
                        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
                        final HttpClientBuilder builderWithManager = b.setConnectionManager(httpClientConnectionManager);
                        connManager = httpClientConnectionManager;

                        return createHttpClient(builderWithManager, endpoint, getProperty("login"), getProperty("password"));
                    }){});


    protected static PoolingHttpClientConnectionManager connManager;

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

    @AfterSuite
    public static void removeProjectAndLogout() {
        if (project != null) {
            gd.getProjectService().removeProject(project);
        }
        gd.logout();
    }
}
