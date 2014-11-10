/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

/**
 * Enum containing implemented connector types.
 */
public enum ConnectorType {

    ZENDESK4;

    public String getName() {
        return name().toLowerCase();
    }

}
