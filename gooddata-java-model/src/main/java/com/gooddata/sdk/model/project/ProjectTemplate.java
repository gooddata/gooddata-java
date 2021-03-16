/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
