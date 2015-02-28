package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Encapsulates identifier and its uri.
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
