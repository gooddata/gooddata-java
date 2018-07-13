/*
 * Copyright (C) 2004-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.executeafm.resultspec

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class TotalLocatorItemTest extends Specification {

    private static final String TOTAL_LOCATOR_ITEM_JSON = 'executeafm/resultspec/totalLocatorItem.json'

    def "should serialize"() {
        expect:
        that new TotalLocatorItem('aId', 'avg'),
                jsonEquals(resource(TOTAL_LOCATOR_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        TotalLocatorItem item = readObjectFromResource("/$TOTAL_LOCATOR_ITEM_JSON".toString(), TotalLocatorItem)

        then:
        item.attributeIdentifier == 'aId'
        item.totalType == 'avg'
        item.toString()
    }
}
