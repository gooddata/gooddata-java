/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm

import com.gooddata.sdk.model.executeafm.afm.*
import com.gooddata.sdk.model.executeafm.afm.filter.ExpressionFilter
import com.gooddata.sdk.model.executeafm.resultspec.*
import com.gooddata.sdk.model.md.report.Total
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class NestedExecutionTest extends Specification {

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')
    private static final String EXECUTION_JSON = 'executeafm/nestedExecution.json'
    private static final String EXECUTION_FULL_JSON = 'executeafm/nestedExecutionFull.json'

    def "should serialize"() {
        expect:
        that new NestedExecution(
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
        that new NestedExecution(
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
        NestedExecution computation = readObjectFromResource("/$EXECUTION_JSON", NestedExecution)

        then:
        computation.afm != null
        computation.resultSpec == null
        computation.toString()
    }

    def "should deserialize full"() {
        when:
        NestedExecution computation = readObjectFromResource("/$EXECUTION_FULL_JSON", NestedExecution)

        then:
        computation.afm != null
        computation.resultSpec != null
        computation.toString()
    }

    def "should change resultSpec"() {
        given:
        NestedExecution computation = new NestedExecution(new Afm())
        ResultSpec spec = new ResultSpec()

        when:
        computation.resultSpec = spec

        then:
        computation.resultSpec == spec
    }
}
