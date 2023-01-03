/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ExecutionResultTest extends Specification {

    private static final String EXECUTION_RESULT_JSON = 'executeafm/result/executionResult.json'
    private static final String EXECUTION_RESULT_FULL_JSON = 'executeafm/result/executionResultFull.json'

    private static final String[][] DATA = [
            ["-12958511.8099999", "25315434.8199999", "-2748323.76", "-7252542.67"],
            ["1234", "5853", "12340", "9999"]
    ]

    def "should serialize"() {
        expect:
        that new ExecutionResult(DATA,
                new Paging([2, 4], [0, 0], [2, 4])),
                jsonEquals(resource(EXECUTION_RESULT_JSON))
    }

    def "should deserialize"() {
        when:
        ExecutionResult result = readObjectFromResource("/$EXECUTION_RESULT_JSON", ExecutionResult)

        then:
        result.paging.count == [2, 4]
        result.paging.offset == [0, 0]
        result.paging.total == [2, 4]
    }

    def "should serialize full"() {
        expect:
        that new ExecutionResult(new DataList(DATA.collect{
                    def values = it.collect { new DataValue(it) }
                    new DataList(values)
                }),
                new Paging([2, 4], [0, 0], [2, 4]),
                [
                        [[new AttributeHeaderItem('Employee1', '/gdc/md/FoodMartDemo/obj/122/elements?id=123'),
                          new AttributeHeaderItem('Employee2', '/gdc/md/FoodMartDemo/obj/122/elements?id=456')]],
                        [[new AttributeHeaderItem('Cost of Goods Sold', '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'),
                          new AttributeHeaderItem('Salaries', '/gdc/md/FoodMartDemo/obj/124/elements?id=6000')]]
                ],
                [[['25']], [['38']]],
                [[['43']]],
                [new Warning('gdc123', 'Some msg %s %s %s', ['bum', 1, null])]),
                jsonEquals(resource(EXECUTION_RESULT_FULL_JSON))
    }

    def "should deserialize full"() {
        when:
        ExecutionResult result = readObjectFromResource("/$EXECUTION_RESULT_FULL_JSON", ExecutionResult)

        then:
        result.paging.count == [2, 4]
        result.paging.offset == [0, 0]
        result.paging.total == [2, 4]

        def accountHeaderItems = result.headerItems
        accountHeaderItems[0][0][0].uri == '/gdc/md/FoodMartDemo/obj/122/elements?id=123'
        accountHeaderItems[0][0][0].name == 'Employee1'
        accountHeaderItems[0][0][1].uri == '/gdc/md/FoodMartDemo/obj/122/elements?id=456'
        accountHeaderItems[0][0][1].name == 'Employee2'
        accountHeaderItems[1][0][0].uri == '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'
        accountHeaderItems[1][0][0].name == 'Cost of Goods Sold'
        accountHeaderItems[1][0][1].uri == '/gdc/md/FoodMartDemo/obj/124/elements?id=6000'
        accountHeaderItems[1][0][1].name == 'Salaries'

        def totals = result.totals
        totals[0][0][0] == '25'
        totals[1][0][0] == '38'

        def totalTotals = result.totalTotals
        totalTotals[0][0][0] == '43'

        def warnings = result.warnings
        warnings == [new Warning('gdc123', 'Some msg %s %s %s', ['bum', 1, null])]

        result.toString()
    }

    def "should set properties"() {
        when:
        ExecutionResult result = new ExecutionResult([1] as String[], new Paging())
        result.addHeaderItems([[new AttributeHeaderItem("n", "u")]])
        result.setTotals([[[1]]])
        result.setWarnings([new Warning('gdc123', 'Some msg %s %s %s', ['bum', 1, null])])

        then:
        result.getHeaderItems()[0][0][0].name == "n"
        result.getHeaderItems()[0][0][0].uri == "u"

        result.getTotals() == [[[1]]]

        result.getWarnings() == [new Warning('gdc123', 'Some msg %s %s %s', ['bum', 1, null])]
    }
}
