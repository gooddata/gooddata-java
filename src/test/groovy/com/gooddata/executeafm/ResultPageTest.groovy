/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

class ResultPageTest extends Specification {

    @Unroll
    def "should not create with #args"() {
        when:
        new ResultPage(offsets, limits)

        then:
        thrown(IllegalArgumentException)

        where:
        args             | offsets | limits
        'null offsets'   | null    | [1]
        'null limits'    | [1]     | null
        'empty offsets'  | []      | [1]
        'empty limits'   | [1]     | []
        'different size' | [1, 2]  | [1]
    }

    def "should get query params"() {
        when:
        def page = new ResultPage([1, 2], [7, 8])

        then:
        page.offsetsQueryParam == '1%2C2'
        page.limitsQueryParam == '7%2C8'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(ResultPage)
                .usingGetClass()
                .verify()
    }
}
