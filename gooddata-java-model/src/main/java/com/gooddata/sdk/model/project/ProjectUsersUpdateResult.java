/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

/**
 * Result of project users update
 */
@JsonTypeName("projectUsersUpdateResult")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectUsersUpdateResult {

    private final List<String> successful;
    private final List<String> failed;

    @JsonCreator
    private ProjectUsersUpdateResult(@JsonProperty("successful") final List<String> successful,
                                     @JsonProperty("failed") final List<String> failed) {
        this.successful = successful;
        this.failed = failed;
    }

    /**
     *
     * @return List of account URIs successfully updated
     */
    public List<String> getSuccessful() {
        return successful;
    }

    /**
     *
     * @return List of account URIs failed to update
     */
    public List<String> getFailed() {
        return failed;
    }
}
