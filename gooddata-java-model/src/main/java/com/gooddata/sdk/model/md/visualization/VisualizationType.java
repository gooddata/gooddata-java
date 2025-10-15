/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 * Represents supported types of currently used visualizations
 */
public enum VisualizationType {
    TABLE,
    LINE,
    COLUMN,
    BAR,
    PIE;

    public static VisualizationType of(final String type) {
        try {
            return VisualizationType.valueOf(notNull(type, "type").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(
                    format("Unknown visualization type: \"%s\", supported types are: [%s]",
                            type, stream(VisualizationType.values()).map(Enum::name).collect(joining(","))),
                    e);
        }
    }
}

