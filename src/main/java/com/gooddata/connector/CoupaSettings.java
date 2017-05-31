/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Coupa connector settings (contains timezone only)
 */
@JsonTypeName("coupaSettings")
public class CoupaSettings implements Settings {

    public static final String URL = "/gdc/projects/{project}/connectors/coupa/integration/config/settings";

    private final String timeZone;

    @JsonCreator
    public CoupaSettings(@JsonProperty("timeZone") String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public ConnectorType getConnectorType() {
        return ConnectorType.COUPA;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
