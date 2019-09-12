/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource

class ExtendedFilterTest extends Specification {

    private static final String POSITIVE_ATTRIBUTE_FILTER_JSON = '/executeafm/afm/positiveAttributeFilter.json'
    private static final String NEGATIVE_ATTRIBUTE_FILTER_JSON = '/executeafm/afm/negativeAttributeFilter.json'
    private static final String ABSOLUTE_DATE_FILTER_JSON = '/executeafm/afm/absoluteDateFilter.json'
    private static final String RELATIVE_DATE_FILTER_JSON = '/executeafm/afm/relativeDateFilter.json'
    private static final String MEASURE_VALUE_FILTER_JSON = '/executeafm/afm/measureValueFilter.json'

    @Unroll
    def "should deserialize as #type"() {
        when:
        ExtendedFilter filter = readObjectFromResource(jsonPath, ExtendedFilter)

        then:
        typeClass.isInstance(filter)

        where:
        typeClass               | jsonPath
        PositiveAttributeFilter | POSITIVE_ATTRIBUTE_FILTER_JSON
        NegativeAttributeFilter | NEGATIVE_ATTRIBUTE_FILTER_JSON
        AbsoluteDateFilter      | ABSOLUTE_DATE_FILTER_JSON
        RelativeDateFilter      | RELATIVE_DATE_FILTER_JSON
        MeasureValueFilter      | MEASURE_VALUE_FILTER_JSON

        type = typeClass.simpleName
    }
}
