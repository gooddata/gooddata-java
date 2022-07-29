/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm

import com.gooddata.sdk.model.executeafm.afm.Afm
import com.gooddata.sdk.model.executeafm.afm.AttributeItem
import com.gooddata.sdk.model.executeafm.afm.filter.ExpressionFilter
import com.gooddata.sdk.model.executeafm.afm.MeasureItem
import com.gooddata.sdk.model.executeafm.afm.NativeTotalItem
import com.gooddata.sdk.model.executeafm.afm.SimpleMeasureDefinition
import com.gooddata.sdk.model.executeafm.resultspec.AttributeLocatorItem
import com.gooddata.sdk.model.executeafm.resultspec.AttributeSortItem
import com.gooddata.sdk.model.executeafm.resultspec.Dimension
import com.gooddata.sdk.model.executeafm.resultspec.Direction
import com.gooddata.sdk.model.executeafm.resultspec.MeasureLocatorItem
import com.gooddata.sdk.model.executeafm.resultspec.MeasureSortItem
import com.gooddata.sdk.model.executeafm.resultspec.ResultSpec
import com.gooddata.sdk.model.executeafm.resultspec.TotalItem
import com.gooddata.sdk.model.md.report.Total
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ExecutionTest extends Specification {

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')
    private static final String EXECUTION_JSON = 'executeafm/execution.json'
    private static final String EXECUTION_FULL_JSON = 'executeafm/executionFull.json'

    def "should serialize"() {
        expect:
        that new Execution(
                new Afm(
                        [new AttributeItem(QUALIFIER, 'a1')],
                        [new ExpressionFilter('some expression')],
                        [new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'm1')],
                        [new NativeTotalItem('mId', ['a1', 'a2'])]
                )
        ),
                jsonEquals(resource(EXECUTION_JSON))
    }

    def "should serialize full"() {
        expect:
        that new Execution(
                new Afm(
                        [new AttributeItem(QUALIFIER, 'a1')],
                        [new ExpressionFilter('some expression')],
                        [new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'm1')],
                        [new NativeTotalItem('mId', ['a1', 'a2'])]
                ),
                new ResultSpec(
                        [new Dimension(['i1'], [new TotalItem('mId', Total.AVG, 'a1')] as Set)],
                        [
                                new AttributeSortItem(Direction.ASC, 'aId'),
                                new MeasureSortItem(Direction.ASC,
                                        new MeasureLocatorItem('mId'),
                                        new AttributeLocatorItem('aId', 'a1'))
                        ]
                )
        ),
                jsonEquals(resource(EXECUTION_FULL_JSON))
    }

    def "should deserialize"() {
        when:
        Execution computation = readObjectFromResource("/$EXECUTION_JSON", Execution)

        then:
        computation.afm != null
        computation.resultSpec == null
        computation.toString()
    }

    def "should deserialize full"() {
        when:
        Execution computation = readObjectFromResource("/$EXECUTION_FULL_JSON", Execution)

        then:
        computation.afm != null
        computation.resultSpec != null
        computation.toString()
    }

    def "should change resultSpec"() {
        given:
        Execution computation = new Execution(new Afm())
        ResultSpec spec = new ResultSpec()

        when:
        computation.resultSpec = spec

        then:
        computation.resultSpec == spec
    }
}
