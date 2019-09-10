/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;

/**
 * {@link AttributeFilterElements} represented by values of elements.
 */
public final class ValueAttributeFilterElements implements AttributeFilterElements, Serializable {

    private static final long serialVersionUID = 8162844914489089022L;
    static final String NAME = "values";

    private final List<String> values;

    /**
     * Creates new instance of given attribute elements' values.
     * @param values elements' values.
     */
    @JsonCreator
    public ValueAttributeFilterElements(final List<String> values) {
        this.values = values;
    }

    /**
     * Creates new instance of given attribute elements' values.
     * @param values elements' values.
     */
    public ValueAttributeFilterElements(String... values) {
        this(asList(values));
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public List<String> getElements() {
        return getValues();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueAttributeFilterElements that = (ValueAttributeFilterElements) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
