/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Encapsulates list of identifiers and their URIs.
 * Deserialization only.
 */
public class IdentifiersAndUris {

    public static final String URI = "/gdc/md/{projectId}/identifiers";

    private final List<IdentifierAndUri> identifiersAndUris;

    @JsonCreator
    IdentifiersAndUris(@JsonProperty("identifiers") List<IdentifierAndUri> identifiersAndUris) {
        this.identifiersAndUris = identifiersAndUris;
    }

    public List<String> getUris() {
        return identifiersAndUris.stream().map(IdentifierAndUri::getUri).collect(Collectors.toList());
    }

    @Deprecated
    public Map<String, String> asMap() {
        return asIdentifierToUri();
    }

    /**
     * Get values as identifier to URI map.
     *
     * @return map with identifiers as keys, URI as values
     */
    public Map<String, String> asIdentifierToUri() {
        if (identifiersAndUris == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> identifierToUri = identifiersAndUris.stream()
                .collect(Collectors.toMap(IdentifierAndUri::getIdentifier, IdentifierAndUri::getUri));
        return Collections.unmodifiableMap(identifierToUri);
    }

    /**
     * Get values as URI to identifier map.
     *
     * @return map with URI as keys, identifiers as values
     */
    public Map<String, String> asUriToIdentifier() {
        if (identifiersAndUris == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> uriToIdentifier = identifiersAndUris.stream()
                .collect(Collectors.toMap(IdentifierAndUri::getUri, IdentifierAndUri::getIdentifier));
        return Collections.unmodifiableMap(uriToIdentifier);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
