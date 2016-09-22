/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

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
