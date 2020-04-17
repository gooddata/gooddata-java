/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm

import com.gooddata.sdk.common.GoodDataException
import com.gooddata.sdk.model.executeafm.Execution
import com.gooddata.sdk.model.executeafm.ResultPage
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import com.gooddata.sdk.model.executeafm.VisualizationExecution
import com.gooddata.sdk.model.executeafm.afm.Afm
import com.gooddata.sdk.model.executeafm.afm.AttributeItem
import com.gooddata.sdk.model.executeafm.afm.MeasureItem
import com.gooddata.sdk.model.executeafm.afm.SimpleMeasureDefinition
import com.gooddata.sdk.model.executeafm.response.AttributeHeader
import com.gooddata.sdk.model.executeafm.response.AttributeInHeader
import com.gooddata.sdk.model.executeafm.response.ExecutionResponse
import com.gooddata.sdk.model.executeafm.response.MeasureGroupHeader
import com.gooddata.sdk.model.executeafm.response.MeasureHeaderItem
import com.gooddata.sdk.model.executeafm.response.ResultDimension
import com.gooddata.sdk.model.executeafm.result.ExecutionResult
import com.gooddata.sdk.model.executeafm.result.Paging
import com.gooddata.sdk.model.project.Project
import com.gooddata.sdk.service.GoodDataITBase
import spock.lang.Shared
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.jadler.Jadler.onRequest
import static net.jadler.Jadler.verifyThatRequest
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals

class ExecuteAfmServiceIT extends GoodDataITBase<ExecuteAfmService> {

    private static final String DF_URI = '/gdc/md/PROJECT_ID/obj/1'
    private static final String ATTR_URI = '/gdc/md/PROJECT_ID/obj/2'
    private static final String MEASURE_URI = '/gdc/md/PROJECT_ID/obj/3'
    private static final UriObjQualifier ATTR_QUALIFIER = new UriObjQualifier(DF_URI)
    private static final UriObjQualifier MEASURE_QUALIFIER = new UriObjQualifier(MEASURE_URI)

    private static final String RESULT_PATH = '/gdc/app/projects/PROJECT_ID/executionResults/6042010690076179456'
    private static final String RESULT_PAGE = '&offset=0%2C0&limit=1000%2C1000'
    private static final String RESULT_QUERY_BASE = "c=123&q=456&dimensions=2&totals=0%2C0"
    private static final String RESULT_QUERY = "$RESULT_QUERY_BASE$RESULT_PAGE"
    private static final String RESULT_URI = "$RESULT_PATH?$RESULT_QUERY"

    @Shared
    Project project = readObjectFromResource('/project/project.json', Project)

    @Shared
    Execution afmExecution = new Execution(new Afm()
                    .addAttribute(new AttributeItem(ATTR_QUALIFIER, 'a1'))
                    .addMeasure(new MeasureItem(new SimpleMeasureDefinition(MEASURE_QUALIFIER), 'm1')))

    @Shared
    VisualizationExecution visualizationExecution = new VisualizationExecution('/gdc/md/PROJECT_ID/obj/4')

    @Shared
    ExecutionResponse response = new ExecutionResponse([
            new ResultDimension(new AttributeHeader('name', 'a1', DF_URI, 'a1DfId', new AttributeInHeader('aName', ATTR_URI, 'a1Id'))),
            new ResultDimension(new MeasureGroupHeader([new MeasureHeaderItem('mName', 'f', 'm1', MEASURE_URI, 'm1Id')]))
    ], RESULT_URI)

    def "should execute AFM"() {
        given:
        onRequest()
                .havingMethodEqualTo('POST')
                .havingPathEqualTo('/gdc/app/projects/PROJECT_ID/executeAfm')
                .havingBody(jsonEquals(afmExecution))
        .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(response))
                .withStatus(200)

        when:
        ExecutionResponse executed = service.executeAfm(project, afmExecution)

        then:
        executed?.dimensions?.size() == 2
        executed?.executionResultUri == RESULT_URI
    }

    def "should handle failed AFM execution request"() {
        given:
        onRequest()
                .havingMethodEqualTo('POST')
                .havingPathEqualTo('/gdc/app/projects/PROJECT_ID/executeAfm')
                .havingBody(jsonEquals(afmExecution))
        .respond()
                .withStatus(400)

        when:
        service.executeAfm(project, afmExecution)

        then:
        def ex = thrown(GoodDataException)
        ex.message == 'Unable to execute AFM'
    }

    @Unroll
    "should get execution result #order page"() {
        given:
        ExecutionResult result = new ExecutionResult(new String[0], new Paging([0], [0], [0]))

        onRequest()
                .respond()
                .withStatus(202)
         .thenRespond()
                .withBody(OBJECT_MAPPER.writeValueAsString(result))
                .withStatus(200)

        when:
        getResult(service).get()

        then:
        verifyThatRequest()
                .havingMethodEqualTo('GET')
                .havingPathEqualTo(RESULT_PATH)
                .havingQueryStringEqualTo(resultQuery)
                .receivedTimes(2)

        where:
        order | resultQuery                                     | getResult
        '1st' | RESULT_QUERY                                    | {it.getResult(response)}
        '2nd' | "$RESULT_QUERY_BASE&offset=1%2C0&limit=10%2C10" | {it.getResult(response, new ResultPage([1, 0], [10, 10]))}
    }

    @Unroll
    "should handle failed result with status #statusCode"() {
        given:
        onRequest()
            .havingMethodEqualTo('GET')
            .havingPathEqualTo(RESULT_PATH)
            .havingQueryStringEqualTo(RESULT_QUERY)
        .respond()
            .withStatus(statusCode)

        when:
        service.getResult(response).get()

        then:
        def ex = thrown(ExecutionResultException)
        ex ==~ pattern

        where:
        statusCode | pattern
        400        | /.*is not computable.*/
        410        | /.*result no longer available.*/
        413        | /.*result is too large.*/
        500        | /.*failed.*unknown.*reason.*/

    }

    def "should execute visualization object"() {
        given:
        onRequest()
                .havingMethodEqualTo('POST')
                .havingPathEqualTo('/gdc/app/projects/PROJECT_ID/executeVisualization')
                .havingBody(jsonEquals(visualizationExecution))
        .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(response))
                .withStatus(200)

        when:
        ExecutionResponse executed = service.executeVisualization(project, visualizationExecution)

        then:
        executed?.dimensions?.size() == 2
        executed?.executionResultUri == RESULT_URI
    }

    def "should handle failed visualization execution request"() {
        given:
        onRequest()
                .havingMethodEqualTo('POST')
                .havingPathEqualTo('/gdc/app/projects/PROJECT_ID/executeVisualization')
                .havingBody(jsonEquals(visualizationExecution))
        .respond()
                .withStatus(400)

        when:
        service.executeVisualization(project, visualizationExecution)

        then:
        def ex = thrown(GoodDataException)
        ex.message == 'Unable to execute visualization'
    }

    @Override
    protected ExecuteAfmService getService() {
        return gd.executeAfmService
    }
}
