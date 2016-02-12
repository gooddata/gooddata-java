/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.util.UriTemplate;

/**
 * ETL Pull input DTO (for internal use).
 * Serialization only.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class Pull {

    public static final String URI = "/gdc/md/{projectId}/etl/pull2";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    @JsonProperty("pullIntegration")
    private final String remoteDir;


    public Pull(String remoteDir) {
        this.remoteDir = remoteDir;
    }

    public String getRemoteDir() {
        return remoteDir;
    }
}
