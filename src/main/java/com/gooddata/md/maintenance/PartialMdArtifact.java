package com.gooddata.md.maintenance;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.gdc.UriResponse;

/**
 * Partial metadata export result structure.
 * For internal use only.
 */
@JsonTypeName("partialMDArtifact")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
class PartialMdArtifact {

    @JsonProperty("status")
    private final UriResponse status;
    private final String token;

    @JsonCreator
    public PartialMdArtifact(@JsonProperty("status") UriResponse status, @JsonProperty("token") String token) {
        this.status = status;
        this.token = token;
    }

    @JsonIgnore
    public String getStatusUri() {
        return status.getUri();
    }

    public String getToken() {
        return token;
    }
}
