/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Project template.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectTemplate {

    public static final String URI = "/gdc/md/{projectId}/templates";

    private final String url;
    private final String urn;
    private final String version;

    @JsonCreator
    public ProjectTemplate(@JsonProperty("url") String url, @JsonProperty("urn") String urn,
                           @JsonProperty("version") String version) {
        this.url = url;
        this.urn = urn;
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public String getUrn() {
        return urn;
    }

    public String getVersion() {
        return version;
    }

}
