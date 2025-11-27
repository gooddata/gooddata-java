/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

/**
 * Enum containing implemented connector types (currently version 4 of Zendesk connector only).
 */
public enum ConnectorType {

    ZENDESK4(Zendesk4Settings.URL);

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
