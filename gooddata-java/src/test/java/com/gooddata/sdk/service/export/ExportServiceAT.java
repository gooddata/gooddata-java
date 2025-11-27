/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export;

import com.gooddata.sdk.model.export.ExportFormat;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExportServiceAT extends AbstractGoodDataAT {

    private ExportService service;

    @BeforeMethod
    public void setUp() throws Exception {
        service = gd.getExportService();
    }

    @Test(groups = "export", dependsOnGroups = "dataset")
    public void exportReportDefinition() throws Exception {
        service.export(reportDefinition, ExportFormat.CSV, System.out);
    }

    @Test(groups = "export", dependsOnGroups = "dataset")
    public void shouldExportDashboard() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.exportPdf(endpoint, dashboard, dashboard.getTabs().iterator().next(), output).get();
        assertThat(output, is(notNullValue()));
    }

    @Test(groups = "export", dependsOnGroups = "dataset")
    public void shouldReportDefinitionRaw() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.exportCsv(reportDefinition, output).get();
        assertThat(output, is(notNullValue()));
    }
}