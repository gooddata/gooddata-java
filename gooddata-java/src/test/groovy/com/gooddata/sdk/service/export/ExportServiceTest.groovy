/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export

import com.gooddata.sdk.model.md.ProjectDashboard
import com.gooddata.sdk.model.md.report.Report
import com.gooddata.sdk.model.md.report.ReportDefinition
import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import org.springframework.web.reactive.function.client.WebClient
import org.mockito.Mockito
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class ExportServiceTest extends Specification {

    def webClient = Mock(WebClient)
    @Subject ExportService service = new ExportService(webClient, new GoodDataSettings())

    @Shared ProjectDashboard dashboard = readObjectFromResource('/md/projectDashboard.json', ProjectDashboard)
    @Shared ProjectDashboard.Tab tab = dashboard.tabs.first()
    @Shared Report report = readObjectFromResource('/md/report/report.json', Report)
    @Shared ReportDefinition reportDefinition = readObjectFromResource('/md/report/gridReportDefinition.json', ReportDefinition)

    def "should not create"() {
        when:
        new ExportService(null, new GoodDataSettings())

        then:
        thrown(IllegalArgumentException)
    }

    def "should fail exportPdf on invalid arguments"() {
        when:
        service.exportPdf(endpoint, dashboardArg, tabArg, output)

        then:
        def exc = thrown(IllegalArgumentException)
        exc.message =~ msgRegex

        where:
        endpoint               | dashboardArg | tabArg | output                      | msgRegex
        null                   | dashboard    | tab    | new ByteArrayOutputStream() | /.*endpoint.*/
        new GoodDataEndpoint() | null         | tab    | new ByteArrayOutputStream() | /.*dashboard.*/
        new GoodDataEndpoint() | dashboard    | null   | new ByteArrayOutputStream() | /.*tab.*/
        new GoodDataEndpoint() | dashboard    | tab    | null                        | /.*output.*/
    }

    def "should fail exportCsv report on invalid arguments"() {
        when:
        service.exportCsv(toExport as Report, output)

        then:
        def exc = thrown(IllegalArgumentException)
        exc.message =~ msgRegex

        where:
        toExport | output                      | msgRegex
        null     | new ByteArrayOutputStream() | /.*report.*/
        report   | null                        | /.*output.*/
    }

    def "should fail exportCsv reportDefinition on invalid arguments"() {
        when:
        service.exportCsv(toExport as ReportDefinition, output)

        then:
        def exc = thrown(IllegalArgumentException)
        exc.message =~ msgRegex

        where:
        toExport         | output                      | msgRegex
        null             | new ByteArrayOutputStream() | /.*reportDefinition.*/
        reportDefinition | null                        | /.*output.*/
    }

    def "should extract project id"() {
        expect:
        ExportService.extractProjectId(dashboard) == 'PROJECT_ID'
    }

    def "should fail extract project id"() {
        when:
        ExportService.extractProjectId(null)

        then:
        thrown(IllegalArgumentException)
    }


}
