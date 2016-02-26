package com.gooddata.md.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.gdc.UriResponse;

/**
 * Partial metadata export result structure.
 * For internal use only.
 */
@JsonTypeName("partialMDArtifact")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
class PartialMdArtifact {

    private final UriResponse status;
    private final String token;

    @JsonCreator
    public PartialMdArtifact(@JsonProperty("status") UriResponse status, @JsonProperty("token") String token) {
        this.status = status;
        this.token = token;
    }

    public UriResponse getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
