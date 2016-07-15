package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Encapsulates list of identifiers and their URIs.
 * Deserialization only.
 */
class IdentifiersAndUris {

    public static final String URI = "/gdc/md/{projectId}/identifiers";

    private final List<IdentifierAndUri> identifiersAndUris;

    @JsonCreator
    IdentifiersAndUris(@JsonProperty("identifiers") List<IdentifierAndUri> identifiersAndUris) {
        this.identifiersAndUris = identifiersAndUris;
    }

    public List<String> getUris() {
        final List<String> uris = new ArrayList<>();
        for (IdentifierAndUri idAndUri : identifiersAndUris) {
            uris.add(idAndUri.getUri());
        }
        return uris;
    }

    public Map<String, String> getIdentifiersAndUrisMap() {
        final Map<String, String> identifiersToUris = new HashMap<>();
        for (IdentifierAndUri idAndUri : identifiersAndUris) {
            identifiersToUris.put(idAndUri.getIdentifier(), idAndUri.getUri());
        }
        return Collections.unmodifiableMap(identifiersToUris);
    }
}
