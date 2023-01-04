/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export

import com.gooddata.sdk.model.export.ExportFormat
import com.gooddata.sdk.model.export.ReportRequest
import com.gooddata.sdk.model.gdc.AsyncTask
import com.gooddata.sdk.model.gdc.UriResponse
import com.gooddata.sdk.model.md.ProjectDashboard
import com.gooddata.sdk.model.md.report.Report
import com.gooddata.sdk.model.md.report.ReportDefinition
import com.gooddata.sdk.service.GoodDataITBase
import spock.lang.Shared
import spock.lang.Unroll

import java.nio.charset.StandardCharsets

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.jadler.Jadler.onRequest
import static net.jadler.Jadler.port

class ExportServiceIT extends GoodDataITBase<ExportService> {

    private static final String EXPORT_POLL_URI = "$ExportService.EXPORTING_URI/123"

    private static final String CLIENT_EXPORT = '/gdc/projects/PROJECT_ID/clientexport'
    private static final String CLIENT_EXPORT_POLL = "$CLIENT_EXPORT/123"

    private static final String RAW_EXPORT = '/gdc/projects/PROJECT_ID/execute/raw'
    private static final String RAW_EXPORT_POLL = "$RAW_EXPORT/123"

    private static final String RESPONSE = "abc";

    @Shared ReportDefinition DEFINITION = readObjectFromResource('/md/report/gridReportDefinition.json', ReportDefinition)
    @Shared Report REPORT = readObjectFromResource('/md/report/report.json', Report)
    @Shared ProjectDashboard DASHBOARD = readObjectFromResource('/md/projectDashboard.json', ProjectDashboard)

    def setup() {
        onRequest()
                .havingPathEqualTo(ReportRequest.URI)
                .havingMethodEqualTo('POST')
            .respond()
                .withBody('{}')
        onRequest()
                .havingPathEqualTo(ExportService.EXPORTING_URI)
                .havingMethodEqualTo('POST')
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("http://localhost:${port()}$EXPORT_POLL_URI")))
        onRequest()
                .havingPathEqualTo(EXPORT_POLL_URI)
                .havingMethodEqualTo('GET')
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
        ;
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT)
                .havingMethodEqualTo('POST')
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask("http://localhost:${port()}$CLIENT_EXPORT_POLL")))
        onRequest()
                .havingPathEqualTo(CLIENT_EXPORT_POLL)
                .havingMethodEqualTo('GET')
            .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
        ;
        onRequest()
                .havingPathEqualTo(RAW_EXPORT)
                .havingMethodEqualTo('POST')
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("http://localhost:${port()}$RAW_EXPORT_POLL")))
        onRequest()
                .havingPathEqualTo(RAW_EXPORT_POLL)
                .havingMethodEqualTo('GET')
                .respond()
                .withStatus(202)
            .thenRespond()
                .withStatus(200)
                .withBody(RESPONSE)
    }

    @Unroll
    def "should export #type"() {
        when:
        def output = new ByteArrayOutputStream()
        service.export(toExport, ExportFormat.CSV, output).get()

        then:
        output.toString(StandardCharsets.US_ASCII.name()) == RESPONSE

        where:
        type         | toExport
        'definition' | DEFINITION
        'report'     | REPORT
    }

    def "should fail export csv"() {
        given:
        onRequest()
                .havingPathEqualTo(EXPORT_POLL_URI)
                .havingMethodEqualTo('GET')
            .respond()
                .withStatus(status)

        when:
        service.export(REPORT, ExportFormat.CSV, new ByteArrayOutputStream()).get()

        then:
        def exc = thrown(exceptionType)
        exc.message =~ msgRegex

        where:
        status | exceptionType         | msgRegex
        400    | ExportException       | /Unable to export report/
        204    | NoDataExportException | /Export contains no data/
    }

    def "should export dashboard"() {
        when:
        def output = new ByteArrayOutputStream();
        service.exportPdf(endpoint, DASHBOARD, DASHBOARD.tabs.first(), output).get()

        then:
        output.toString(StandardCharsets.US_ASCII.name()) == RESPONSE
    }

    def "should fail export dashboard"() {
        given:
        onRequest()
                .havingPathEqualTo(path)
                .havingMethodEqualTo(method)
            .respond()
                .withStatus(400)

        when:
        service.exportPdf(endpoint, DASHBOARD, DASHBOARD.tabs.first(), new ByteArrayOutputStream()).get()

        then:
        thrown(ExportException)

        where:
        path               | method
        CLIENT_EXPORT      | 'POST'
        CLIENT_EXPORT_POLL | 'GET'
    }

    def "should export raw"() {
        when:
        def output = new ByteArrayOutputStream()
        service.exportCsv(REPORT, output).get()

        then:
        output.toString(StandardCharsets.US_ASCII.name()) == RESPONSE
    }

    def "should fail export raw"() {
        given:
        onRequest()
                .havingPathEqualTo(path)
                .havingMethodEqualTo(method)
            .respond()
                .withStatus(status)

        when:
        service.exportCsv(REPORT, new ByteArrayOutputStream()).get()

        then:
        thrown(exceptionClass)

        where:
        path            | method | status | exceptionClass
        RAW_EXPORT      | 'POST' | 400    | ExportException
        RAW_EXPORT_POLL | 'GET'  | 204    | NoDataExportException
        RAW_EXPORT_POLL | 'GET'  | 400    | ExportException

    }

    @Override
    protected ExportService getService() {
        return gd.exportService
    }
}
