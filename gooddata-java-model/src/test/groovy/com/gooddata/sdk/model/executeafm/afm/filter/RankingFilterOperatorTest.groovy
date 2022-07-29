/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.executeafm.afm.filter

import spock.lang.Specification
import spock.lang.Unroll

class RankingFilterOperatorTest extends Specification {

    @Unroll
    def "'of' method should throw #exception when invalid value #value is provided"() {
        when:
        RankingFilterOperator.of(value)

        then:
        thrown(exception)

        where:
        value     | exception
        'UNKNOWN' | UnsupportedOperationException
        null      | IllegalArgumentException
    }

    @Unroll
    def "'of' method should resolve the correct enum value when string '#name' is provided"() {
        when:
        def resolvedOperator = RankingFilterOperator.of(name)

        then:
        resolvedOperator == operator

        where:
        operator                     | name
        RankingFilterOperator.TOP    | 'TOP'
        RankingFilterOperator.BOTTOM | 'BOTTOM'
    }

    @Unroll
    def "'toString' method should return correct representation of #operator"() {
        when:
        def resolvedName = RankingFilterOperator.of(name)

        then:
        resolvedName.toString() == name

        where:
        operator                     | name
        RankingFilterOperator.TOP    | 'TOP'
        RankingFilterOperator.BOTTOM | 'BOTTOM'
    }
}
