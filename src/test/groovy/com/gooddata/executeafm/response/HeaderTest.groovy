/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource

class HeaderTest extends Specification {

    @Unroll
    def "should deserialize as #type"() {
        when:
        Header header = readObjectFromResource("/executeafm/response/${type}.json", Header)

        then:
        typeClass.isInstance(header)

        where:
        typeClass << [AttributeHeader, MeasureGroupHeader]
        type = typeClass.simpleName.uncapitalize()
    }
}
