/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class MeasureValueFilterConditionTest extends Specification {

    private static final String COMPARISON_CONDITION_JSON = '/executeafm/afm/comparisonConditionGreaterThanOperator.json'
    private static final String RANGE_CONDITION_JSON = '/executeafm/afm/rangeConditionBetweenOperator.json'

    @Unroll
    def "should deserialize as #type"() {
        when:
        MeasureValueFilterCondition condition = readObjectFromResource(jsonPath, MeasureValueFilterCondition)

        then:
        typeClass.isInstance(condition)

        where:
        typeClass           | jsonPath
        ComparisonCondition | COMPARISON_CONDITION_JSON
        RangeCondition      | RANGE_CONDITION_JSON

        type = typeClass.simpleName
    }
}

