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

import static RangeConditionOperator.BETWEEN
import static RangeConditionOperator.NOT_BETWEEN
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class RangeConditionTest extends Specification {

    private static final String RANGE_CONDITION_BETWEEN_OPERATOR_JSON = 'executeafm/afm/rangeConditionBetweenOperator.json'
    private static final String RANGE_CONDITION_NOT_BETWEEN_OPERATOR_JSON = 'executeafm/afm/rangeConditionNotBetweenOperator.json'
    private static final String RANGE_CONDITION_INVALID_OPERATOR_JSON = 'executeafm/afm/rangeConditionInvalidOperator.json'

    def "should serialize"() {
        expect:
        that new RangeCondition(BETWEEN, -12, 42069.669),
                jsonEquals(resource(RANGE_CONDITION_BETWEEN_OPERATOR_JSON))
    }

    def "should create RangeCondition with RangeConditionOperator enum"() {
        when:
        RangeCondition rangeCondition = new RangeCondition(BETWEEN, -12.3, 42069.669)

        then:
        with(rangeCondition) {
            operator == BETWEEN
            from == -12.3
            to == 42069.669
        }
    }

    @Unroll
    def "should deserialize condition with #conditionOperator operator"() {
        when:
        RangeCondition rangeCondition = readObjectFromResource("/$jsonPath", RangeCondition)

        then:
        with(rangeCondition) {
            operator == conditionOperator
            from == conditionFrom
            to == conditionTo
        }
        rangeCondition.toString()

        where:
        jsonPath                                  | conditionOperator | conditionFrom | conditionTo
        RANGE_CONDITION_BETWEEN_OPERATOR_JSON     | BETWEEN           | -12           | 42069.669
        RANGE_CONDITION_NOT_BETWEEN_OPERATOR_JSON | NOT_BETWEEN       | 12            | 4999
    }

    @Unroll
    def "test serializable of condition with #operator operator"() {
        given:
        RangeCondition rangeCondition = readObjectFromResource("/$jsonPath", RangeCondition)
        RangeCondition deserialized = SerializationUtils.roundtrip(rangeCondition)

        expect:
        that deserialized, jsonEquals(rangeCondition)

        where:
        jsonPath                                  | operator
        RANGE_CONDITION_BETWEEN_OPERATOR_JSON     | BETWEEN
        RANGE_CONDITION_NOT_BETWEEN_OPERATOR_JSON | NOT_BETWEEN
    }

    def "should deserialize object without exception in case of unknown operator"() {
        given:
        RangeCondition rangeCondition = readObjectFromResource("/$RANGE_CONDITION_INVALID_OPERATOR_JSON", RangeCondition)
        RangeCondition deserialized = SerializationUtils.roundtrip(rangeCondition)

        expect:
        with(deserialized) {
            stringOperator == 'INVALID_OPERATOR'
            from == -12
            to == 42069.669
        }
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(RangeCondition).usingGetClass().verify()
    }
}
