/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.resultspec

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeSortItemTest extends Specification {

    private static final String ATTRIBUTE_SORT_ITEM_JSON = 'executeafm/resultspec/attributeSortItem.json'
    private static final String ATTRIBUTE_SORT_ITEM_WITH_AGGREGATION_JSON = 'executeafm/resultspec/attributeSortItemWithAggregation.json'


    def "should serialize values"() {
        expect:
        that new AttributeSortItem(Direction.ASC, 'aId'),
                jsonEquals(resource(ATTRIBUTE_SORT_ITEM_JSON))
    }

    def "should serialize values with aggregation"() {
        expect:
        that new AttributeSortItem(Direction.ASC, 'aId', AttributeSortAggregation.SUM),
                jsonEquals(resource(ATTRIBUTE_SORT_ITEM_WITH_AGGREGATION_JSON))
    }

    def "should deserialize values"() {
        when:
        AttributeSortItem item = readObjectFromResource("/$ATTRIBUTE_SORT_ITEM_WITH_AGGREGATION_JSON", AttributeSortItem)

        then:
        item.attributeIdentifier == 'aId'
        item.direction == 'asc'
        item.aggregation == 'sum'
        item.toString()
    }
}
