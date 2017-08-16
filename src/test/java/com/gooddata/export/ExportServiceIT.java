/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.report.ReportExportFormat;
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

    private static final String URI = ExportService.EXPORTING_URI + "/123";
    private static final String RESPONSE = "abc";
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
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("http://localhost:" + port() + URI)));
        onRequest()
                .havingPathEqualTo(URI)
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
        final ReportDefinition rd = readObjectFromResource("/md/report/gridReportDefinition.json", ReportDefinition.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(rd, ExportFormat.CSV, output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test
    public void shouldExportReport() throws Exception {
        final Report rd = readObjectFromResource("/md/report/report.json", Report.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(rd, ExportFormat.CSV, output).get();
        assertThat(output.toString(StandardCharsets.US_ASCII.name()), is(RESPONSE));
    }

    @Test(expectedExceptions = ExportException.class, expectedExceptionsMessageRegExp = "Unable to export report")
    public void shouldFail() throws Exception {
        onRequest()
                .havingPathEqualTo(URI)
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(400);

        final Report rd = readObjectFromResource("/md/report/report.json", Report.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(rd, ExportFormat.CSV, output).get();
    }

    @Test(expectedExceptions = NoDataExportException.class, expectedExceptionsMessageRegExp = "Export contains no data")
    public void shouldFailNoData() throws Exception {
        onRequest()
                .havingPathEqualTo(URI)
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(204);

        final Report rd = readObjectFromResource("/md/report/report.json", Report.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.export(rd, ExportFormat.CSV, output).get();
    }
}