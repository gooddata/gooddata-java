/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.visualization;

/**
 * Represents type of collection that can be used as local identifier for {@link Bucket}
 */
public enum CollectionType {
    SEGMENT,
    STACK,
    TREND,
    VIEW;

    boolean isValueOf(final String type) {
        return name().toLowerCase().equals(type.toLowerCase());
    }
}
