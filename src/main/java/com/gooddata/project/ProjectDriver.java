/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.project;

import static org.apache.commons.lang.Validate.notEmpty;

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
