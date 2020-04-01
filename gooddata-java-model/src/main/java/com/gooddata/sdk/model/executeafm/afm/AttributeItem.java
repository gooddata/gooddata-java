/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.md.AttributeDisplayForm;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents attribute within {@link Afm}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeItem implements LocallyIdentifiable, Serializable {

    private static final long serialVersionUID = -1484150046473673413L;
    private final String localIdentifier;
    private final ObjQualifier displayForm;

    private String alias;

    /**
     * Creates new instance
     * @param displayForm qualifier of {@link AttributeDisplayForm} representing the attribute
     * @param localIdentifier local identifier, unique within {@link Afm}
     * @param alias attribute alias
     */
    @JsonCreator
    public AttributeItem(@JsonProperty("displayForm") final ObjQualifier displayForm,
                         @JsonProperty("localIdentifier") final String localIdentifier,
                         @JsonProperty("alias") final String alias) {
        this.localIdentifier = localIdentifier;
        this.displayForm = displayForm;
        this.alias = alias;
    }

    /**
     * Creates new instance
     * @param displayForm qualifier of {@link AttributeDisplayForm} representing the attribute
     * @param localIdentifier local identifier, unique within {@link Afm}
     */
    public AttributeItem(final ObjQualifier displayForm, final String localIdentifier) {
        this.displayForm = displayForm;
        this.localIdentifier = localIdentifier;
    }

    @Override
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * @return qualifier of {@link AttributeDisplayForm} representing the attribute
     */
    public ObjQualifier getDisplayForm() {
        return displayForm;
    }

    /**
     * @return attribute alias (used as header in result)
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets attribute alias (used as header in result)
     * @param alias alias
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeItem that = (AttributeItem) o;
        return Objects.equals(localIdentifier, that.localIdentifier) &&
                Objects.equals(displayForm, that.displayForm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localIdentifier, displayForm);
    }
}

