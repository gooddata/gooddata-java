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

    ZENDESK4(Zendesk4Settings.URL),
    COUPA(CoupaSettings.URL),
    PARDOT(PardotSettings.URL);

    //URL of the settings endpoint (which is not equal for individual connector types)
    private final String settingsUrl;

    ConnectorType(String settingsUrl) {
        this.settingsUrl = settingsUrl;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public String getSettingsUrl() {
        return settingsUrl;
    }
}
