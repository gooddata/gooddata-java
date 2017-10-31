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

class MeasureLocatorItemTest extends Specification {

    private static final String MEASURE_LOCATOR_ITEM_JSON = 'executeafm/resultspec/measureLocatorItem.json'

    def "should serialize values"() {
        expect:
        that new MeasureLocatorItem('mId'),
                jsonEquals(resource(MEASURE_LOCATOR_ITEM_JSON))
    }

    def "should deserialize values"() {
        when:
        MeasureLocatorItem item = readObjectFromResource("/$MEASURE_LOCATOR_ITEM_JSON", MeasureLocatorItem)

        then:
        item.measureIdentifier == 'mId'
        item.toString()
    }
}
