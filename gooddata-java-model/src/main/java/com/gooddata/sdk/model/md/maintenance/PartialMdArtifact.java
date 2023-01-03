/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * Partial metadata export result structure.
 * For internal use only.
 */
@JsonTypeName("partialMDArtifact")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartialMdArtifact {

    private final UriResponse status;
    private final String token;

    @JsonCreator
    public PartialMdArtifact(@JsonProperty("status") UriResponse status, @JsonProperty("token") String token) {
        this.status = status;
        this.token = token;
    }

    @JsonIgnore
    public String getStatusUri() {
        return getStatus().getUri();
    }

    public String getToken() {
        return token;
    }

    @JsonProperty("status")
    private UriResponse getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
