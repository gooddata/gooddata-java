/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource

class FilterItemTest extends Specification {

    @Unroll
    def "should deserialize as #type"() {
        when:
        FilterItem filter = readObjectFromResource("/executeafm/afm/${type}.json", FilterItem)

        then:
        typeClass.isInstance(filter)

        where:
        typeClass << [PositiveAttributeFilter, NegativeAttributeFilter,
                      AbsoluteDateFilter, RelativeDateFilter]
        type = typeClass.simpleName.uncapitalize()
    }
}
