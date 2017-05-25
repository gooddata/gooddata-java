/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
