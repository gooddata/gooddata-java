/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import static com.gooddata.util.Validate.notEmpty;

public enum ProjectDriver {
    POSTGRES("Pg"),
    VERTICA("vertica");

    private final String value;

    ProjectDriver(String value) {
        notEmpty(value, "value cannot be empty!");
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
