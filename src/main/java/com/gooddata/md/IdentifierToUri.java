package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * Strucuture with list of symbolic names to be expanded by related uri.
 */
class IdentifierToUri {

    private final Collection<String> identifierToUri;

    @JsonCreator
    IdentifierToUri(@JsonProperty("identifierToUri") final Collection<String> identifierToUri) {
        notNull(identifierToUri, "identifierToUri");
        this.identifierToUri = identifierToUri;
    }

    public Collection<String> getIdentifierToUri() {
        return identifierToUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifierToUri that = (IdentifierToUri) o;

        if (!identifierToUri.equals(that.identifierToUri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifierToUri.hashCode();
    }
}
