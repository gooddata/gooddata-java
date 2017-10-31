/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

/**
 * Represents attribute within {@link ObjectAfm}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeItem implements LocallyIdentifiable {

    private final String localIdentifier;
    private final ObjQualifier displayForm;
    private String alias;

    /**
     * Creates new instance
     * @param displayForm qualifier of {@link com.gooddata.md.AttributeDisplayForm} representing the attribute
     * @param localIdentifier local identifier, unique within {@link ObjectAfm}
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
     * @param displayForm qualifier of {@link com.gooddata.md.AttributeDisplayForm} representing the attribute
     * @param localIdentifier local identifier, unique within {@link ObjectAfm}
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
     * @return qualifier of {@link com.gooddata.md.AttributeDisplayForm} representing the attribute
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
}

