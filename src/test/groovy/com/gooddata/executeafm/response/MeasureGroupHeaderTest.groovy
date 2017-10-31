/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class MeasureGroupHeaderTest extends Specification {

    private static final String MEASURE_GROUP_HEADER_JSON = 'executeafm/response/measureGroupHeader.json'
    private static final String MEASURE_GROUP_HEADER_FULL_JSON = 'executeafm/response/measureGroupHeaderFull.json'

    def "should serialize full"() {
        expect:
        that new MeasureGroupHeader([new MeasureHeaderItem('Name', '#,##0.00', 'm1')], [new TotalHeaderItem('sum')]),
                jsonEquals(resource(MEASURE_GROUP_HEADER_FULL_JSON))
    }

    def "should serialize"() {
        expect:
        that new MeasureGroupHeader([new MeasureHeaderItem('Name', '#,##0.00', 'm1')]),
                jsonEquals(resource(MEASURE_GROUP_HEADER_JSON))
    }

    def "should deserialize"() {
        when:
        MeasureGroupHeader header = readObjectFromResource("/$MEASURE_GROUP_HEADER_JSON", MeasureGroupHeader)

        then:
        header.items?.size() == 1
        header.items.first().localIdentifier == 'm1'
    }

    def "should deserialize full"() {
        when:
        MeasureGroupHeader header = readObjectFromResource("/$MEASURE_GROUP_HEADER_FULL_JSON", MeasureGroupHeader)

        then:
        header.items?.size() == 1
        header.items.first().localIdentifier == 'm1'
        header.totalItems.size() == 1
        header.totalItems.first().name == 'sum'
        header.toString()
    }
}
