/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export;

import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.ProjectDashboard.Tab;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportServiceTest {

    private final ExportService service = new ExportService(new RestTemplate(), new GoodDataEndpoint(), new GoodDataSettings());
    private ProjectDashboard dashboard;
    private Tab tab;
    private Report report;

    @BeforeMethod
    public void setUp() throws Exception {
        dashboard = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);
        report = readObjectFromResource("/md/report/report.json", Report.class);
        tab = dashboard.getTabs().iterator().next();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullArgument() throws Exception {
        new ExportService(null, new GoodDataEndpoint(), new GoodDataSettings());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*endpoint.*")
    public void shouldFailOnNullEndpoint() throws Exception {
        new ExportService(new RestTemplate(), null, new GoodDataSettings());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*dashboard.*")
    public void shouldFailExportPdfOnNullDashboard() throws Exception {
        service.exportPdf(null, tab, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*tab.*")
    public void shouldFailExportPdfOnNullTab() throws Exception {
        service.exportPdf(dashboard, null, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*output.*")
    public void shouldFailExportPdfOnNullStream() throws Exception {
        service.exportPdf(dashboard, tab, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*report.*")
    public void shouldFailExportCsvOnNullReport() throws Exception {
        service.exportCsv((Report)null, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*reportDefinition.*")
    public void shouldFailExportCsvOnNullReportDefinition() throws Exception {
        service.exportCsv((ReportDefinition) null, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*output.*")
    public void shouldFailExportCsvOnNullStream() throws Exception {
        service.exportCsv(report, null);
    }

    @Test
    public void shouldExtractProjectId() throws Exception {
        assertThat(ExportService.extractProjectId(dashboard), is("PROJECT_ID"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*obj.uri.*")
    public void shouldFailExtractProjectIdOnNullDashboardUri() throws Exception {
        final ProjectDashboard dashboard = mock(ProjectDashboard.class);
        ExportService.extractProjectId(dashboard);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*obj uri - project id.*")
    public void shouldFailExtractProjectIdOnInvalidDashboardUri() throws Exception {
        final ProjectDashboard dashboard = mock(ProjectDashboard.class);
        when(dashboard.getUri()).thenReturn("/foo");
        ExportService.extractProjectId(dashboard);
    }
}