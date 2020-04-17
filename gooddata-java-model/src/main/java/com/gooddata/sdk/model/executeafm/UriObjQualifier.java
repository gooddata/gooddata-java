/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.sdk.model.md.Obj;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * Qualifies metadata {@link Obj} using an URI
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
