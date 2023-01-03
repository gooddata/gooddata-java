/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Qualifies AFM object using an local identifier
 */
@JsonRootName("localIdentifier")
public final class LocalIdentifierQualifier implements Qualifier, Serializable {

    private static final long serialVersionUID = -7856385638703759024L;

    private final String localIdentifier;

    /**
     * Creates a new instance of {@link LocalIdentifierQualifier}.
     *
     * @param localIdentifier The local identifier value.
     */
    public LocalIdentifierQualifier(final String localIdentifier) {
        this.localIdentifier = notNull(localIdentifier, "localIdentifier");
    }

    /**
     * @return local identifier value
     */
    @JsonValue
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LocalIdentifierQualifier that = (LocalIdentifierQualifier) o;
        return Objects.equals(localIdentifier, that.localIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localIdentifier);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
