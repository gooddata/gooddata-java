/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.LocalIdentifierQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.GREATER_THAN
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class MeasureValueFilterTest extends Specification {

    private static final String UNDEFINED_MEASURE_VALUE_FILTER_JSON = '/executeafm/afm/undefinedMeasureValueFilter.json'
    private static final String COMPARISON_MEASURE_VALUE_FILTER_JSON = '/executeafm/afm/comparisonMeasureValueFilter.json'
    private static final String RANGE_MEASURE_VALUE_FILTER_JSON = '/executeafm/afm/rangeMeasureValueFilter.json'

    @Unroll
    def "should deserialize filter with #title"() {
        when:
        MeasureValueFilter measureValueFilter = readObjectFromResource(jsonPath, MeasureValueFilter)

        then:
        MeasureValueFilter.isInstance(measureValueFilter)
        measureValueFilter.measure == new LocalIdentifierQualifier('local.identifier')
        measureValueFilter.getCondition() in typeClass

        where:
        typeClass           | jsonPath                             | title
        null                | UNDEFINED_MEASURE_VALUE_FILTER_JSON  | "Undefined condition"
        ComparisonCondition | COMPARISON_MEASURE_VALUE_FILTER_JSON | ComparisonCondition.simpleName
        RangeCondition      | RANGE_MEASURE_VALUE_FILTER_JSON      | RangeCondition.simpleName
    }

    def "should copy"() {
        when:
        def filter = new MeasureValueFilter(new IdentifierObjQualifier("id"), new ComparisonCondition(GREATER_THAN, 12.1))
        def copy = filter.withUriObjQualifier(new UriObjQualifier("uri"))

        then:
        copy.measure.uri == "uri"
        copy.condition == new ComparisonCondition(GREATER_THAN, 12.1)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(MeasureValueFilter).usingGetClass().verify()
    }
}
