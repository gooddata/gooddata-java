/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.*;
import java.util.stream.Collectors;

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
        return identifiersAndUris.stream().map(IdentifierAndUri::getUri).collect(Collectors.toList());
    }

    public Map<String, String> asMap() {
        final Map<String, String> identifiersToUris = new HashMap<>();
        for (IdentifierAndUri idAndUri : identifiersAndUris) {
            identifiersToUris.put(idAndUri.getIdentifier(), idAndUri.getUri());
        }
        return Collections.unmodifiableMap(identifiersToUris);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
