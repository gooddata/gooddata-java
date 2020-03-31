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
    private static final String COMPARISON_CONDITION_INVALID_JSON = 'executeafm/afm/comparisonConditionInvalidOperator.json'
    private static final String COMPARISON_CONDITION_TREAT_NULL_AS_ZERO_JSON = 'executeafm/afm/comparisonConditionWithTreatNullValueAsZero.json'
    private static final String COMPARISON_CONDITION_TREAT_NULL_AS_DECIMAL_JSON = 'executeafm/afm/comparisonConditionWithTreatNullValueAsNegativeDecimal.json'

    def "should serialize"() {
        expect:
        that new ComparisonCondition(GREATER_THAN, -42.154),
                jsonEquals(resource(COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON))
    }

    def "should create ComparisonCondition with ComparisonConditionOperator enum"() {
        when:
        ComparisonCondition comparisonCondition = new ComparisonCondition(GREATER_THAN, 100.0)

        then:
        with(comparisonCondition) {
            operator == GREATER_THAN
            value == 100.0
            treatNullValuesAs == null
        }
    }

    def "should create ComparisonCondition with treatNullValuesAs"() {
        when:
        ComparisonCondition comparisonCondition = new ComparisonCondition(GREATER_THAN, 100.0, 0)

        then:
        with(comparisonCondition) {
            operator == GREATER_THAN
            value == 100.0
            treatNullValuesAs == 0
        }
    }

    @Unroll
    def "should deserialize condition with #conditionOperator operator"() {
        when:
        ComparisonCondition comparisonCondition = readObjectFromResource("/$jsonPath", ComparisonCondition)

        then:
        with(comparisonCondition) {
            operator == conditionOperator
            value == conditionValue
            treatNullValuesAs == conditionTreatNullValuesAs
        }
        comparisonCondition.toString()

        where:
        jsonPath                                                    | conditionOperator        | conditionValue | conditionTreatNullValuesAs
        COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON             | GREATER_THAN             | -42.154        | null
        COMPARISON_CONDITION_GREATER_THAN_OR_EQUAL_TO_OPERATOR_JSON | GREATER_THAN_OR_EQUAL_TO | 698.12364      | null
        COMPARISON_CONDITION_LESS_THAN_OPERATOR_JSON                | LESS_THAN                | 1557887.25     | null
        COMPARISON_CONDITION_LESS_THAN_OR_EQUAL_TO_OPERATOR_JSON    | LESS_THAN_OR_EQUAL_TO    | 1254           | null
        COMPARISON_CONDITION_EQUAL_TO_OPERATOR_JSON                 | EQUAL_TO                 | 12             | null
        COMPARISON_CONDITION_NOT_EQUAL_TO_JSON                      | NOT_EQUAL_TO             | 0              | null
        COMPARISON_CONDITION_TREAT_NULL_AS_ZERO_JSON                | EQUAL_TO                 | 12             | 0
        COMPARISON_CONDITION_TREAT_NULL_AS_DECIMAL_JSON             | EQUAL_TO                 | 12             | -12.6
    }

    @Unroll
    def "test serializable of condition with #operator operator and #treatNullValueAs as treatNullValueAs"() {
        given:
        ComparisonCondition comparisonCondition = readObjectFromResource("/$jsonPath", ComparisonCondition)
        ComparisonCondition deserialized = SerializationUtils.roundtrip(comparisonCondition)

        expect:
        that deserialized, jsonEquals(comparisonCondition)

        where:
        jsonPath                                                    | operator                 | treatNullValueAs
        COMPARISON_CONDITION_GREATER_THAN_OPERATOR_JSON             | GREATER_THAN             | null
        COMPARISON_CONDITION_GREATER_THAN_OR_EQUAL_TO_OPERATOR_JSON | GREATER_THAN_OR_EQUAL_TO | null
        COMPARISON_CONDITION_LESS_THAN_OPERATOR_JSON                | LESS_THAN                | null
        COMPARISON_CONDITION_LESS_THAN_OR_EQUAL_TO_OPERATOR_JSON    | LESS_THAN_OR_EQUAL_TO    | null
        COMPARISON_CONDITION_EQUAL_TO_OPERATOR_JSON                 | EQUAL_TO                 | null
        COMPARISON_CONDITION_NOT_EQUAL_TO_JSON                      | NOT_EQUAL_TO             | null
        COMPARISON_CONDITION_TREAT_NULL_AS_ZERO_JSON                | EQUAL_TO                 | 0
        COMPARISON_CONDITION_TREAT_NULL_AS_DECIMAL_JSON             | EQUAL_TO                 | -12.6
    }

    def "should deserialize object without exception in case of unknown operator"() {
        given:
        ComparisonCondition comparisonCondition = readObjectFromResource("/$COMPARISON_CONDITION_INVALID_JSON", ComparisonCondition)
        ComparisonCondition deserialized = SerializationUtils.roundtrip(comparisonCondition)

        expect:
        with(deserialized) {
            stringOperator == 'INVALID_OPERATOR'
            value == 100
            treatNullValuesAs == null
        }
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(ComparisonCondition).usingGetClass().verify()
    }
}
