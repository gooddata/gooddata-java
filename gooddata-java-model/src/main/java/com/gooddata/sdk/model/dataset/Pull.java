/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * ETL Pull input DTO (for internal use).
 * Serialization only.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pull {

    public static final String URI = "/gdc/md/{projectId}/etl/pull2";

    @JsonProperty("pullIntegration")
    private final String remoteDir;


    public Pull(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getRemoteDir() {
        return remoteDir;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
