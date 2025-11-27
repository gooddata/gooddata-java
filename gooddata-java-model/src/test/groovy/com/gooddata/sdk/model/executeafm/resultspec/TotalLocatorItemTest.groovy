/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.resultspec

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
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
