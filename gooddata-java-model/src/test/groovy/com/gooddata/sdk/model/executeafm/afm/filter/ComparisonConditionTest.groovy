/*
 * Copyright (C) 2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.EQUAL_TO
import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.GREATER_THAN
import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.GREATER_THAN_OR_EQUAL_TO
import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.LESS_THAN
import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.LESS_THAN_OR_EQUAL_TO
import static com.gooddata.sdk.model.executeafm.afm.filter.ComparisonConditionOperator.NOT_EQUAL_TO
import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ComparisonConditionTest extends Specification {

    private static final String COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON = 'executeafm/afm/comparisonConditionGreaterThanOperator.json'
    private static final String COMPARISON_CONDITION_GREATER_THAN_OR_EQUAL_TO_OPERATOR_JSON = 'executeafm/afm/comparisonConditionGreaterThanOrEqualToOperator.json'
    private static final String COMPARISON_CONDITION_LESS_THAN_OPERATOR_JSON = 'executeafm/afm/comparisonConditionLessThanOperator.json'
    private static final String COMPARISON_CONDITION_LESS_THAN_OR_EQUAL_TO_OPERATOR_JSON = 'executeafm/afm/comparisonConditionLessThanOrEqualToOperator.json'
    private static final String COMPARISON_CONDITION_EQUAL_TO_OPERATOR_JSON = 'executeafm/afm/comparisonConditionEqualToOperator.json'
    private static final String COMPARISON_CONDITION_NOT_EQUAL_TO_JSON = 'executeafm/afm/comparisonConditionNotEqualToOperator.json'

    def "should serialize"() {
        expect:
        that new ComparisonCondition(GREATER_THAN, -42.154),
                jsonEquals(resource(COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON))
    }

    @Unroll
    def "should deserialize condition with #conditionOperator operator"() {
        when:
        ComparisonCondition comparisonCondition = readObjectFromResource("/$jsonPath", ComparisonCondition)

        then:
        with(comparisonCondition) {
            operator == conditionOperator
            value == conditionValue
        }
        comparisonCondition.toString()

        where:
        jsonPath                                                    | conditionOperator        | conditionValue
        COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON             | GREATER_THAN             | -42.154
        COMPARISON_CONDITION_GREATER_THAN_OR_EQUAL_TO_OPERATOR_JSON | GREATER_THAN_OR_EQUAL_TO | 698.12364
        COMPARISON_CONDITION_LESS_THAN_OPERATOR_JSON                | LESS_THAN                | 1557887.25
        COMPARISON_CONDITION_LESS_THAN_OR_EQUAL_TO_OPERATOR_JSON    | LESS_THAN_OR_EQUAL_TO    | 1254
        COMPARISON_CONDITION_EQUAL_TO_OPERATOR_JSON                 | EQUAL_TO                 | 12
        COMPARISON_CONDITION_NOT_EQUAL_TO_JSON                      | NOT_EQUAL_TO             | 0
    }

    @Unroll
    def "test serializable of condition with #operator operator"() {
        given:
        ComparisonCondition comparisonCondition = readObjectFromResource("/$jsonPath", ComparisonCondition)
        ComparisonCondition deserialized = SerializationUtils.roundtrip(comparisonCondition)

        expect:
        that deserialized, jsonEquals(comparisonCondition)

        where:
        jsonPath                                                    | operator
        COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON             | GREATER_THAN
        COMPARISON_CONDITION_GREATER_THAN_OR_EQUAL_TO_OPERATOR_JSON | GREATER_THAN_OR_EQUAL_TO
        COMPARISON_CONDITION_LESS_THAN_OPERATOR_JSON                | LESS_THAN
        COMPARISON_CONDITION_LESS_THAN_OR_EQUAL_TO_OPERATOR_JSON    | LESS_THAN_OR_EQUAL_TO
        COMPARISON_CONDITION_EQUAL_TO_OPERATOR_JSON                 | EQUAL_TO
        COMPARISON_CONDITION_NOT_EQUAL_TO_JSON                      | NOT_EQUAL_TO
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(ComparisonCondition).usingGetClass().verify()
    }
}
