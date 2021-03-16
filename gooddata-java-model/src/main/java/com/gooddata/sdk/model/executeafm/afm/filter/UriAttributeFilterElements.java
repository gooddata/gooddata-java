/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * {@link AttributeFilterElements} represented by uris of elements.
 */
public final class UriAttributeFilterElements implements AttributeFilterElements, Serializable {

    private static final long serialVersionUID = -588170788038973574L;
    static final String JSON_ROOT_NAME = "uris";

    private final List<String> uris;

    /**
     * Creates new instance of given attribute elements' uris.
     * @param uris elements' uris.
     */
    @JsonCreator
    public UriAttributeFilterElements(final List<String> uris) {
        this.uris = uris;
    }

    /**
     * Creates new instance of given attribute elements' uris.
     * @param uris elements' uris.
     */
    public UriAttributeFilterElements(String... uris) {
        this(asList(uris));
    }

    public List<String> getUris() {
        return uris;
    }

    @Override
    public List<String> getElements() {
        return getUris();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UriAttributeFilterElements that = (UriAttributeFilterElements) o;
        return Objects.equals(uris, that.uris);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uris);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
