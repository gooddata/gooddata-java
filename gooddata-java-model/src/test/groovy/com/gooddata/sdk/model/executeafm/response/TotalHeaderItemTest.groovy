/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import com.gooddata.sdk.model.md.report.Total
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class TotalHeaderItemTest extends Specification {

    private static final String TOTAL_HEADER_ITEM_JSON = 'executeafm/response/totalHeaderItem.json'

    def "should serialize"() {
        expect:
        that new TotalHeaderItem(Total.AVG),
                jsonEquals(resource(TOTAL_HEADER_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        TotalHeaderItem item = readObjectFromResource("/$TOTAL_HEADER_ITEM_JSON", TotalHeaderItem)

        then:
        item.name == 'avg'
        item.toString()
    }
}
