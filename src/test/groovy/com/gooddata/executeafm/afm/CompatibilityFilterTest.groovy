/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource

class CompatibilityFilterTest extends Specification {

    @Unroll
    def "should deserialize as #type"() {
        when:
        CompatibilityFilter filter = readObjectFromResource("/executeafm/afm/${type}.json", CompatibilityFilter)

        then:
        typeClass.isInstance(filter)

        where:
        typeClass << [ExpressionFilter,
                      PositiveAttributeFilter, NegativeAttributeFilter,
                      AbsoluteDateFilter, RelativeDateFilter]
        type = typeClass.simpleName.uncapitalize()
    }
}
