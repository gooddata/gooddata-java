/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;

import static com.gooddata.util.Validate.notNull;

/**
 * Structure with list of symbolic names (identifiers) to be expanded to list of URIs.
 * Serialization only.
 */
class IdentifierToUri {

    private final Collection<String> identifiers;

    IdentifierToUri(final Collection<String> identifiers) {
        notNull(identifiers, "identifiers");
        this.identifiers = identifiers;
    }

    @JsonProperty("identifierToUri")
    public Collection<String> getIdentifiers() {
        return identifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifierToUri that = (IdentifierToUri) o;

        if (!identifiers.equals(that.identifiers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifiers.hashCode();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
