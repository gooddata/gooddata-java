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
 * Represents all the possible operators of {@link RangeCondition}.
 */
public enum RangeConditionOperator {
    BETWEEN,
    NOT_BETWEEN;

    @JsonValue
    @Override
    public String toString() {
        return name();
    }

    @JsonCreator
    public static RangeConditionOperator of(String operator) {
        notNull(operator, "operator");
        try {
            return RangeConditionOperator.valueOf(operator);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(
                format("Unknown value for range condition operator: \"%s\", supported values are: [%s]",
                    operator, stream(RangeConditionOperator.values()).map(Enum::name).collect(joining(","))),
                e);
        }
    }
}
