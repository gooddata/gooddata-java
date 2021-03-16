/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * Encapsulates identifier and its URI.
 */
class IdentifierAndUri {

    private final String identifier;

    private final String uri;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
