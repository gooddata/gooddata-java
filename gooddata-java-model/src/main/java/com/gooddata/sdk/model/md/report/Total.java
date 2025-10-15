/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Represents type of Total for {@link AttributeInGrid}
 */
public enum Total {
    SUM,
    AVG,
    MAX,
    MIN,
    NAT,
    MED;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Total of(String total) {
        notNull(total, "total");
        try {
            return Total.valueOf(total.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(
                    format("Unknown value for Grid's total: \"%s\", supported values are: [%s]",
                            total, stream(Total.values()).map(Enum::name).collect(joining(","))),
                    e);
        }
    }

    /**
     * Ordered list of totals
     *
     * @return ordered list of totals which refers to order of totals data returned by the executor in executionResult
     */
    public static List<Total> orderedValues() {
        return Arrays.asList(SUM, MAX, MIN, AVG, MED, NAT);
    }
}

