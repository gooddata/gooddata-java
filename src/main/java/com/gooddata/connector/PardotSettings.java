/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Pardot connector settings (contains Pardot account ID)
 */
public class PardotSettings implements Settings {

    public static final String URL = "/gdc/projects/{project}/connectors/pardot/integration/config/settings";

    private final String accountId;

    @JsonCreator
    public PardotSettings(@JsonProperty("accountId") String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public ConnectorType getConnectorType() {
        return ConnectorType.PARDOT;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
