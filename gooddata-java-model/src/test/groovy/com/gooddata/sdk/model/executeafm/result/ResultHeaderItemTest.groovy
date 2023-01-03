/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class ResultHeaderItemTest extends Specification {

    @Unroll
    def "should deserialize as #type"() {
        when:
        ResultHeaderItem item = readObjectFromResource("/executeafm/result/${type}.json", ResultHeaderItem)

        then:
        typeClass.isInstance(item)
        item.toString()

        where:
        typeClass << [AttributeHeaderItem, ResultMeasureHeaderItem, ResultTotalHeaderItem]
        type = typeClass.simpleName.uncapitalize()
    }

    def "should allow empty name"() {
        expect:
        new ResultHeaderItem('') {}.name == ''
    }

    def "should not allow null name"() {
        when:
        new ResultHeaderItem(null) {}

        then:
        thrown(IllegalArgumentException)
    }
}
