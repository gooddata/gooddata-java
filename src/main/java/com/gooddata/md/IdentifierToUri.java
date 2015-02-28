package com.gooddata.md;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

import static com.gooddata.util.Validate.notNull;

/**
 * Strucuture with list of symbolic names to be expanded by related uri.
 */
class IdentifierToUri {

    private final List<String> identifierToUri;

    @JsonCreator
    IdentifierToUri(@JsonProperty("identifierToUri") final List<String> identifierToUri) {
        notNull(identifierToUri, "identifierToUri");
        this.identifierToUri = identifierToUri;
    }

    public List<String> getIdentifierToUri() {
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
