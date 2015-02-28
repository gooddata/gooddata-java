package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Encapsulates list of identifier and its uri.
 */
class IdentifiersAndUris {

    public static final String URI = "/gdc/md/{projectId}/identifiers";

    private final List<IdentifierAndUri> identifiers;

    @JsonCreator
    IdentifiersAndUris(@JsonProperty("identifiers") List<IdentifierAndUri> identifiers) {
        this.identifiers = identifiers;
    }

    public List<IdentifierAndUri> getIdentifiers() {
        return identifiers;
    }
}
