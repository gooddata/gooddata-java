/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

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
