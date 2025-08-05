/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry

import com.gooddata.sdk.common.GoodDataException
import com.gooddata.sdk.service.GoodDataITBase
import com.gooddata.sdk.common.GoodDataRestException
import com.gooddata.sdk.service.GoodDataSettings
import com.gooddata.sdk.service.connector.ConnectorException
import com.gooddata.sdk.service.connector.ConnectorService
import com.gooddata.sdk.model.connector.ConnectorType
import com.gooddata.sdk.model.connector.Integration
import com.gooddata.sdk.model.project.Project
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.jadler.Jadler.closeJadler
import static net.jadler.Jadler.onRequest

class RetryableRestTemplateTest extends GoodDataITBase<ConnectorService> {

    private static final long MIN_NO_RETRY_DURATION = 0l
    private static final long MAX_NO_RETRY_DURATION = 2000l
    private static final long MAX_RETRY_DURATION = 10000l
    private static final long MIN_RETRY_DURATION = 3000l

    @Override
    protected ConnectorService getService() {
        return gd.getConnectorService()
    }

    @Override
    protected GoodDataSettings createGoodDataSettings() {
        def retrySettings = new RetrySettings()
        retrySettings.retryCount = 3
        retrySettings.retryInitialInterval = 1400
        retrySettings.retryMaxInterval = 10000
        retrySettings.retryMultiplier = 2

        def settings = new GoodDataSettings()
        settings.setRetrySettings(retrySettings)
        settings.setPollSleep(0)
        return settings
    }

    @Unroll
    def "should retry on #method method and status #errorResponseStatus"() {
        given:
        if (method == 'DELETE') {
            onRequest()
                    .havingMethodEqualTo("DELETE")
                    .havingPathEqualTo("/gdc/projects/PROJECT_ID")
                    .respond()
                        .withStatus(errorResponseStatus)
                    .thenRespond()
                        .withStatus(errorResponseStatus)
                    .thenRespond()
                        .withStatus(204)
        } else if (method == 'GET' && errorResponseStatus == 200) {
            onRequest()
                    .havingMethodEqualTo(method)
                    .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
                    .respond()
                        .withBody(readFromResource("/connector/integration.json"))
                    .thenRespond()
                        .withStatus(500)
        } else {
            onRequest()
                    .havingMethodEqualTo(method)
                    .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
                    .respond()
                        .withStatus(errorResponseStatus)
                    .thenRespond()
                        .withStatus(errorResponseStatus)
                    .thenRespond()
                        .withBody(readFromResource("/connector/integration.json"))
        }

        when:
        def integration = null
        def exception = null
        def startTime = System.currentTimeMillis()
        try {
            def project = readObjectFromResource("/project/project.json", Project.class)
            switch (method) {
                case "GET":
                    integration = getService().getIntegration(project, ConnectorType.ZENDESK4)
                    break
                case "POST":
                    integration = getService().createIntegration(project, ConnectorType.ZENDESK4, new Integration("/some/template"))
                    break
                case "PUT":
                    getService().updateIntegration(project, ConnectorType.ZENDESK4, new Integration("/another/template"))
                    break
                case "DELETE":
                    gd.getProjectService().removeProject(project)
                    break
                default:
                    assert false, "Unknown method '${method}'"
            }
        } catch (Exception e) {
            exception = e
        }
        def endTime = System.currentTimeMillis()
        def length = endTime - startTime

        then:
        length > minTime
        length < maxTime
        if (payloadRetrieved) {
            assert integration != null
        } else {
            assert integration == null
        }
        if (exceptionClass != null) {
            assert exception.getClass() == exceptionClass
        }

        where:
        method   | errorResponseStatus | payloadRetrieved | exceptionClass        | minTime               | maxTime
        "GET"    | 200                 | true             | null                  | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "GET"    | 400                 | false            | GoodDataRestException | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "GET"    | 500                 | true             | null                  | MIN_RETRY_DURATION    | MAX_RETRY_DURATION
        "GET"    | 501                 | false            | GoodDataRestException | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "GET"    | 502                 | true             | null                  | MIN_RETRY_DURATION    | MAX_RETRY_DURATION
        "GET"    | 503                 | true             | null                  | MIN_RETRY_DURATION    | MAX_RETRY_DURATION
        "GET"    | 504                 | true             | null                  | MIN_RETRY_DURATION    | MAX_RETRY_DURATION
        "GET"    | 507                 | true             | null                  | MIN_RETRY_DURATION    | MAX_RETRY_DURATION
        "POST"   | 501                 | false            | ConnectorException    | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "DELETE" | 500                 | false            | GoodDataException     | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "DELETE" | 501                 | false            | GoodDataException     | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "PUT"    | 500                 | false            | GoodDataRestException | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
        "PUT"    | 501                 | false            | GoodDataRestException | MIN_NO_RETRY_DURATION | MAX_NO_RETRY_DURATION
    }

    void cleanup() {
        closeJadler()
    }

}