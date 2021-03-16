/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Represents all the possible operators of {@link ComparisonCondition}
 */
public enum ComparisonConditionOperator {
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    EQUAL_TO,
    NOT_EQUAL_TO;

    @JsonValue
    @Override
    public String toString() {
        return name();
    }

    @JsonCreator
    public static ComparisonConditionOperator of(String operator) {
        notNull(operator, "operator");
        try {
            return ComparisonConditionOperator.valueOf(operator);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(
                format("Unknown value for comparison condition operator: \"%s\", supported values are: [%s]",
                    operator, stream(ComparisonConditionOperator.values()).map(Enum::name).collect(joining(","))),
                e);
        }
    }
}
