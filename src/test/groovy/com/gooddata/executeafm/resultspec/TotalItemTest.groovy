/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.resultspec

import com.gooddata.md.report.Total
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class TotalItemTest extends Specification {

    private static final String TOTAL_ITEM_JSON = 'executeafm/resultspec/totalItem.json'

    def "should serialize"() {
        expect:
        that new TotalItem('mId', Total.AVG, 'a1'),
                jsonEquals(resource(TOTAL_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        TotalItem total = readObjectFromResource("/$TOTAL_ITEM_JSON", TotalItem)

        then:
        total.measureIdentifier == 'mId'
        total.type == Total.AVG.toString()
        total.attributeIdentifier == 'a1'
        total.toString()
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(TotalItem).usingGetClass().verify()
    }
}
