/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Structure with list of URIs to be expanded to list of symbolic names (identifiers).
 * Serialization only.
 *
 * See also {@link IdentifierToUri}.
 */
public class UriToIdentifier {

    private final Collection<String> uris;

    public UriToIdentifier(final Collection<String> uris) {
        notNull(uris, "uris");
        this.uris = uris;
    }

    @JsonProperty("uriToIdentifier")
    public Collection<String> getUris() {
        return uris;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UriToIdentifier that = (UriToIdentifier) o;

        if (!uris.equals(that.uris)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uris.hashCode();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
