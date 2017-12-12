/*
 * Copyright (C) 2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.executeafm;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * Qualifies metadata {@link com.gooddata.md.Obj} using an URI
 */
@JsonRootName("uri")
public final class UriObjQualifier implements ObjQualifier, Serializable {

    private static final long serialVersionUID = 5505403156762360659L;
    private final String uri;

    public UriObjQualifier(final String uri) {
        this.uri = uri;
    }

    @JsonValue
    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof UriObjQualifier)) return false;

        final UriObjQualifier that = (UriObjQualifier) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
