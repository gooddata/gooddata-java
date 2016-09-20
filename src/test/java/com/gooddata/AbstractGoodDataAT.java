/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import com.gooddata.md.Attribute;
import com.gooddata.md.Metric;
import com.gooddata.md.ScheduledMail;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import com.gooddata.authentication.LoginPasswordAuthentication;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.joda.time.LocalDate;
import org.testng.annotations.AfterSuite;

/**
 * Parent for acceptance tests
 */
public abstract class AbstractGoodDataAT {

    protected static final String title =
            "sdktest " + new LocalDate() + " " + System.getenv("BUILD_NUMBER");

    protected static final GoodData gd =
            new GoodData(
                    new GoodDataEndpoint(getProperty("host")),
                    new LoginPasswordAuthentication(getProperty("login"), getProperty("pass")) {
                        /**
                         * had to be overriden to access connectionManager, to test how many connections were not closed
                         */
                        @Override
                        public HttpClient createHttpClient(final GoodDataEndpoint endpoint, final HttpClientBuilder builder) {
                            PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
                            final HttpClientBuilder builderWithManager = builder.setConnectionManager(httpClientConnectionManager);
                            connManager = httpClientConnectionManager;

                            return super.createHttpClient(endpoint, builderWithManager);
                        }
                    });


    protected static PoolingHttpClientConnectionManager connManager;

    protected static String projectToken;
    protected static Project project;

    protected static String fact;
    protected static Attribute attr;
    protected static Metric metric;
    protected static Report report;
    protected static ReportDefinition reportDefinition;
    protected static ScheduledMail scheduledMail;

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
