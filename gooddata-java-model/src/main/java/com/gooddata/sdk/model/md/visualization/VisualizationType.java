/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import org.springframework.util.StringUtils;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;

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
                            type, StringUtils.arrayToCommaDelimitedString(VisualizationType.values())),
                    e);
        }
    }
}
