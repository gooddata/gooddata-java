/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ExecutionResultTest extends Specification {

    private static final String EXECUTION_RESULT_JSON = 'executeafm/result/executionResult.json'
    private static final String EXECUTION_RESULT_FULL_JSON = 'executeafm/result/executionResultFull.json'

    private static final String[] DATA = ["-12958511.8099999", "25315434.8199999", "-2748323.76", "-7252542.67"]

    def "should serialize"() {
        expect:
        that new ExecutionResult(DATA,
                new Paging([4], [0], [4])),
                jsonEquals(resource(EXECUTION_RESULT_JSON))
    }

    def "should deserialize"() {
        when:
        ExecutionResult result = readObjectFromResource("/$EXECUTION_RESULT_JSON", ExecutionResult)

        then:
        result.paging.size == [4]
        result.paging.offset == [0]
        result.paging.total == [4]
    }

    def "should serialize full"() {
        expect:
        that new ExecutionResult(DATA as List,
                new Paging([4], [0], [4]),
                [[[
                  new AttributeHeaderItem('Cost of Goods Sold', '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'),
                  new AttributeHeaderItem('Salaries', '/gdc/md/FoodMartDemo/obj/124/elements?id=6000')]]],
                [[['25']]]),
                jsonEquals(resource(EXECUTION_RESULT_FULL_JSON))
    }

    def "should deserialize full"() {
        when:
        ExecutionResult result = readObjectFromResource("/$EXECUTION_RESULT_FULL_JSON", ExecutionResult)

        then:
        result.paging.size == [4]
        result.paging.offset == [0]
        result.paging.total == [4]

        def accountHeaderItems = result.attributeHeaderItems
        accountHeaderItems[0][0][0].uri == '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'
        accountHeaderItems[0][0][0].name == 'Cost of Goods Sold'
        accountHeaderItems[0][0][1].uri == '/gdc/md/FoodMartDemo/obj/124/elements?id=6000'
        accountHeaderItems[0][0][1].name == 'Salaries'

        def totals = result.totals
        totals[0][0][0] == '25'

        result.toString()
    }

    def "should set properties"() {
        when:
        ExecutionResult result = new ExecutionResult([1] as String[], new Paging())
        result.addAttributeHeaderItems([[new AttributeHeaderItem("n", "u")]])
        result.setTotals([[[1]]])

        then:
        result.getAttributeHeaderItems()[0][0][0].name == "n"
        result.getAttributeHeaderItems()[0][0][0].uri == "u"

        result.getTotals() == [[[1]]]
    }
}
