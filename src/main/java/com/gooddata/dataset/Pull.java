/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
