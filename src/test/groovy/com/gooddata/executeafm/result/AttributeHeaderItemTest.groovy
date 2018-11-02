/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeHeaderItemTest extends Specification {

    private static final String ATTRIBUTE_HEADER_ITEM_JSON = 'executeafm/result/attributeHeaderItem.json'

    def "should serialize"() {
        expect:
        that new AttributeHeaderItem('Cost of Goods Sold', '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'),
                jsonEquals(resource(ATTRIBUTE_HEADER_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        AttributeHeaderItem item = readObjectFromResource("/$ATTRIBUTE_HEADER_ITEM_JSON", AttributeHeaderItem)

        then:
        item.name == 'Cost of Goods Sold'
        item.uri == '/gdc/md/FoodMartDemo/obj/124/elements?id=3200'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(AttributeHeaderItem)
                .usingGetClass()
                .verify()
    }
}
