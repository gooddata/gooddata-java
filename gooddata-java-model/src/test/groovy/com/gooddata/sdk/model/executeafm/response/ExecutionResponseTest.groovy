/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ExecutionResponseTest extends Specification {

    private static final String EXECUTION_RESPONSE_JSON = 'executeafm/response/executionResponse.json'

    private static final AttributeInHeader FORM_OF = new AttributeInHeader('Some attr', '/gdc/md/project_id/obj/567', 'attr.some.id')

    def "should serialize"() {
        expect:
        that new ExecutionResponse(
                [
                        new ResultDimension(
                                new AttributeHeader('Account', 'account', '/gdc/md/FoodMartDemo/obj/124', 'attr.account', FORM_OF),
                                new AttributeHeader('Account Type', 'accountType', '/gdc/md/FoodMartDemo/obj/113', 'attr.accountType', FORM_OF),
                                new MeasureGroupHeader([
                                        new MeasureHeaderItem('Accounting Amount [Sum]', 'format1', 'sum', '/gdc/md/FoodMartDemo/obj/114', 'metric.sum'),
                                        new MeasureHeaderItem('Accounting Amount [Avg]', 'format2', 'avg', '/gdc/md/FoodMartDemo/obj/115', 'metric.avg')
                                ]))
                ], 'poll'),
                jsonEquals(resource(EXECUTION_RESPONSE_JSON))
    }

    def "should deserialize"() {
        when:
        ExecutionResponse response = readObjectFromResource("/$EXECUTION_RESPONSE_JSON", ExecutionResponse)

        then:
        response.links['executionResult'] == 'poll'
        response.executionResultUri == 'poll'

        response.dimensions.size() == 1
        response.dimensions[0].headers.size() == 3

        AttributeHeader account = response.dimensions[0].headers[0] as AttributeHeader
        account.name == 'Account'
        account.uri == '/gdc/md/FoodMartDemo/obj/124'
        account.localIdentifier == 'account'
        account.identifier == 'attr.account'

        AttributeHeader accountType = response.dimensions[0].headers[1] as AttributeHeader
        accountType.name == 'Account Type'
        accountType.uri == '/gdc/md/FoodMartDemo/obj/113'
        accountType.localIdentifier == 'accountType'
        accountType.identifier == 'attr.accountType'

        MeasureGroupHeader measureGroupHeader = response.dimensions[0].headers[2] as MeasureGroupHeader

        List<MeasureHeaderItem> metricHeaderItems = measureGroupHeader.items as List<MeasureHeaderItem>
        metricHeaderItems[0].uri == '/gdc/md/FoodMartDemo/obj/114'
        metricHeaderItems[0].identifier == 'metric.sum'
        metricHeaderItems[0].name == 'Accounting Amount [Sum]'
        metricHeaderItems[0].localIdentifier == 'sum'
        metricHeaderItems[0].format == "format1"
        metricHeaderItems[1].uri == '/gdc/md/FoodMartDemo/obj/115'
        metricHeaderItems[1].identifier == 'metric.avg'
        metricHeaderItems[1].name == 'Accounting Amount [Avg]'
        metricHeaderItems[1].localIdentifier == 'avg'
        metricHeaderItems[1].format == 'format2'

        response.toString()
    }

}

