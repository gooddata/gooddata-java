/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.report;

import java.io.Serializable;

/**
 * Marker element marking the placement of metrics in Grid report.
 * Can be contained either in rows or columns of {@link Grid}.
 */
public final class MetricGroup implements GridElement, Serializable {

    private static final long serialVersionUID = -2971228185501817988L;
    private static final String JSON_VALUE = "metricGroup";

    private final String value;

    public static final MetricGroup METRIC_GROUP = new MetricGroup(JSON_VALUE);

    private MetricGroup(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * @param string string to compare whether is metricGroup
     * @return true when the {@link #METRIC_GROUP}'s string value equals to the argument, false otherwise
     */
    public static boolean equals(String string) {
        return METRIC_GROUP.getValue().equals(string);
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

    @Override
    public String toString() {
        return getValue();
    }
}

