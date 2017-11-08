/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource

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
}
