/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * ETL Pull DTO (for internal use).
 * Serialization only.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Pull {

    public static final String URI = "/gdc/md/{projectId}/etl/pull";

    @JsonProperty("pullIntegration")
    private final String remoteDir;


    public Pull(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getRemoteDir() {
        return remoteDir;
    }
}
