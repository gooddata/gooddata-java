/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

public enum ProjectDriver {
    POSTGRES("Pg"),
    VERTICA("vertica");

    private final String value;

    ProjectDriver(String value) {
        notEmpty(value, "value");
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
