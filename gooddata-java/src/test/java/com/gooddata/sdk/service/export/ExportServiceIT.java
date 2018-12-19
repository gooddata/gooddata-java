/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.model.export.ExportFormat;
import com.gooddata.sdk.model.export.ReportRequest;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExportServiceIT extends AbstractGoodDataIT {

    private static final String EXPORT_POLL_URI = ExportService.EXPORTING_URI + "/123";

    private static final String CLIENT_EXPORT = "/gdc/projects/PROJECT_ID/clientexport";
    private static final String CLIENT_EXPORT_POLL = CLIENT_EXPORT + "/123";

    private static final String RAW_EXPORT = "/gdc/projects/PROJECT_ID/execute/raw";
    private static final String RAW_EXPORT_POLL = RAW_EXPORT + "/123";

    private static final String RESPONSE = "abc";

    private static final ReportDefinition DEFINITION = readObjectFromResource("/md/report/gridReportDefinition.json", ReportDefinition.class);
    private static final Report REPORT = readObjectFromResource("/md/report/report.json", Report.class);
    private static final ProjectDashboard DASHBOARD = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);

    private ExportService service;

    @BeforeMethod
    public void setUp() throws IOException {
        onRequest()
                .havingPathEqualTo(ReportRequest.URI)
                .havingMethodEqualTo("POST")
            .respond()
                .withBody("{}");
        onRequest()
                .havingPathEqualTo(ExportService.EXPORTING_URI)
                .havingMethodEqualTo("POST")
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("http://localhost:" + port() + EXPORT_POLL_URI)));
        onRequest()
                .havingPathEqualTo(EXPORT_POLL_URI)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
        ;
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT)
                .havingMethodEqualTo("POST")
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask("http://localhost:" + port() + CLIENT_EXPORT_POLL)));
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT_POLL)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
        ;
        onRequest()
                .havingPathEqualTo(RAW_EXPORT)
                .havingMethodEqualTo("POST")
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("http://localhost:" + port() + RAW_EXPORT_POLL)));
        onRequest()
                .havingPathEqualTo(RAW_EXPORT_POLL)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
        ;

        service = gd.getExportService();
    }

    @Test
    public void shouldExportReportDefinition() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(DEFINITION, ExportFormat.CSV, output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test
    public void shouldExportReport() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(REPORT, ExportFormat.CSV, output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test(expectedExceptions = ExportException.class, expectedExceptionsMessageRegExp = "Unable to export report")
    public void shouldFail() throws Exception {
        onRequest()
                .havingPathEqualTo(EXPORT_POLL_URI)
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(400);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(REPORT, ExportFormat.CSV, output).get();
    }

    @Test(expectedExceptions = NoDataExportException.class, expectedExceptionsMessageRegExp = "Export contains no data")
    public void shouldFailNoData() throws Exception {
        onRequest()
                .havingPathEqualTo(EXPORT_POLL_URI)
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(204);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(REPORT, ExportFormat.CSV, output).get();
    }

    @Test
    public void shouldExportDashboard() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.exportPdf(DASHBOARD, DASHBOARD.getTabs().iterator().next(), output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test(expectedExceptions = ExportException.class)
    public void shouldFailOnDashboardPost() throws Exception {
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT)
                .havingMethodEqualTo("POST")
            .respond()
                .withStatus(400);

        service.exportPdf(DASHBOARD, DASHBOARD.getTabs().iterator().next(), new ByteArrayOutputStream()).get();
    }

    @Test(expectedExceptions = ExportException.class)
    public void shouldFailOnDashboardPoll() throws Exception {
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT_POLL)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(400);

        service.exportPdf(DASHBOARD, DASHBOARD.getTabs().iterator().next(), new ByteArrayOutputStream()).get();
    }

    @Test
    public void shouldExportRaw() throws Exception {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.exportCsv(REPORT, output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test(expectedExceptions = ExportException.class)
    public void shouldFailOnRawPost() throws Exception {
        onRequest()
                .havingPathEqualTo(RAW_EXPORT)
                .havingMethodEqualTo("POST")
            .respond()
                .withStatus(400);

        service.exportCsv(REPORT, new ByteArrayOutputStream()).get();
    }

    @Test(expectedExceptions = NoDataExportException.class)
    public void shouldFailOnRawPollNoData() throws Exception {
        onRequest()
                .havingPathEqualTo(RAW_EXPORT_POLL)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(204);

        service.exportCsv(REPORT, new ByteArrayOutputStream()).get();
    }

    @Test(expectedExceptions = ExportException.class)
    public void shouldFailOnRawPoll() throws Exception {
        onRequest()
                .havingPathEqualTo(RAW_EXPORT_POLL)
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(400);

        service.exportCsv(REPORT, new ByteArrayOutputStream()).get();
    }

}