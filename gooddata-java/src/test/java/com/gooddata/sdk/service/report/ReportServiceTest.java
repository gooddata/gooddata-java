/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.report;

import com.gooddata.sdk.model.report.ReportExportFormat;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.export.ExportFormat;
import com.gooddata.sdk.service.export.ExportService;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("deprecation")
public class ReportServiceTest {

    private ReportService reportService;
    private ExportService exportService;

    @BeforeMethod
    public void setUp() throws Exception {
        exportService = mock(ExportService.class);
        reportService = new ReportService(exportService, new RestTemplate(), new GoodDataSettings());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullArgument() throws Exception {
        new ReportService(null, mock(RestTemplate.class), new GoodDataSettings());
    }

    @Test
    public void shouldExportReport() throws Exception {
        final Report report = mock(Report.class);

        reportService.exportReport(report, ReportExportFormat.CSV, null);

        verify(exportService).export(eq(report), eq(ExportFormat.CSV), any());
    }

    @Test
    public void shouldExportReportDefinition() throws Exception {
        final ReportDefinition definition = mock(ReportDefinition.class);

        reportService.exportReport(definition, ReportExportFormat.CSV, null);

        verify(exportService).export(eq(definition), eq(ExportFormat.CSV), any());
    }
}