/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result

import com.gooddata.md.report.Total
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ResultTotalHeaderItemTest extends Specification {

    private static final String TOTAL_HEADER_ITEM_JSON = 'executeafm/result/resultTotalHeaderItem.json'

    def "should serialize"() {
        expect:
        that new ResultTotalHeaderItem('Some total', Total.AVG),
                jsonEquals(resource(TOTAL_HEADER_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        ResultTotalHeaderItem item = readObjectFromResource("/$TOTAL_HEADER_ITEM_JSON", ResultTotalHeaderItem)

        then:
        item.name == 'Some total'
        item.type == 'avg'
    }

    def "should create from type only"() {
        expect:
        with(new ResultTotalHeaderItem('avg')) {
            name == 'avg'
            type == 'avg'
        }
        with(new ResultTotalHeaderItem(Total.SUM)) {
            name == 'sum'
            type == 'sum'
        }
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(ResultTotalHeaderItem)
                .usingGetClass()
                .verify()
    }
}
