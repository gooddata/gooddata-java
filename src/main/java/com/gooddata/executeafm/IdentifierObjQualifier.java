/*
 * Copyright (C) 2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.executeafm;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Objects;

/**
 * Qualifies metadata {@link com.gooddata.md.Obj} using an identifier
 */
@JsonRootName("identifier")
public final class IdentifierObjQualifier implements ObjQualifier {
    private final String identifier;

    public IdentifierObjQualifier(final String identifier) {
        this.identifier = identifier;
    }

    @JsonValue
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifierObjQualifier that = (IdentifierObjQualifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
