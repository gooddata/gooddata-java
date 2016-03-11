package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates identifier and its URI.
 */
class IdentifierAndUri {

    private String identifier;

    private String uri;

    @JsonCreator
    IdentifierAndUri(@JsonProperty("identifier") String identifier, @JsonProperty("uri") String uri) {
        this.identifier = identifier;
        this.uri = uri;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUri() {
        return uri;
    }
}
