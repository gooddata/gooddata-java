/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

/**
 * Represents type of dataload process. Please note that this enumeration must not be complete.
 */
public enum ProcessType {
    GRAPH,
    RUBY,
    JAVASCRIPT,
    GROOVY,
    DATALOAD
}
