/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.resultspec

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ResultSpecTest extends Specification {

    private static final String RESULT_SPEC_JSON = 'executeafm/resultspec/resultSpec.json'
    private static final String EMPTY_JSON = 'executeafm/empty.json'

    def "should serialize full"() {
        expect:
        that new ResultSpec(
                [new Dimension('dName', 'i1')],
                [
                        new AttributeSortItem(Direction.ASC, 'aId'),
                        new MeasureSortItem(Direction.ASC,
                                new MeasureLocatorItem('mId'),
                                new AttributeLocatorItem('aId', 'a1'))
                ]
        ),
                jsonEquals(resource(RESULT_SPEC_JSON))
    }

    def "should serialize"() {
        expect:
        that new ResultSpec(),
                jsonEquals(resource(EMPTY_JSON))
    }

    def "should deserialize"() {
        when:
        ResultSpec resultSpec = readObjectFromResource("/$EMPTY_JSON", ResultSpec)

        then:
        resultSpec.dimensions == null
        resultSpec.sorts == null
        resultSpec.toString()
    }

    def "should deserialize full"() {
        when:
        ResultSpec resultSpec = readObjectFromResource("/$RESULT_SPEC_JSON", ResultSpec)

        then:
        with(resultSpec) {
            dimensions[0].name == 'dName'
            dimensions[0].itemIdentifiers == ['i1']
        }

        with(resultSpec.sorts[0] as AttributeSortItem) {
            attributeIdentifier == 'aId'
            direction == 'asc'
        }

        with(resultSpec.sorts[1] as MeasureSortItem) {
            direction == 'asc'
        }
        with((resultSpec.sorts[1] as MeasureSortItem).locators[0] as MeasureLocatorItem) {
            measureIdentifier == 'mId'
        }
        with((resultSpec.sorts[1] as MeasureSortItem).locators[1] as AttributeLocatorItem) {
            attributeIdentifier == 'aId'
            element == 'a1'
        }
    }

    def "should add items"() {
        when:
        def resultSpec = new ResultSpec()
            .addDimension(new Dimension('dName'))
            .addSort(new MeasureSortItem(Direction.ASC))

        then:
        with(resultSpec) {
            dimensions.first().name == 'dName'
            sorts.size() == 1
        }
    }
}
