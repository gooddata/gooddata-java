/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ResultMeasureHeaderItemTest extends Specification {

    private static final String MEASURE_HEADER_ITEM_JSON = 'executeafm/result/resultMeasureHeaderItem.json'

    def "should serialize"() {
        expect:
        that new ResultMeasureHeaderItem('Measure Name', 1),
                jsonEquals(resource(MEASURE_HEADER_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        ResultMeasureHeaderItem item = readObjectFromResource("/$MEASURE_HEADER_ITEM_JSON", ResultMeasureHeaderItem)

        then:
        item.name == 'Measure Name'
        item.order == 1
    }
}
