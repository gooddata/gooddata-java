/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents possible aggregation values at {@link SimpleMeasureDefinition}
 */
public enum Aggregation {

    SUM, COUNT, AVG, MIN, MAX, MEDIAN, RUNSUM;

    @JsonValue
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
