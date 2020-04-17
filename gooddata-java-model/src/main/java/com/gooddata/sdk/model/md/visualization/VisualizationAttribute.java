/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;
import com.gooddata.sdk.model.executeafm.afm.AttributeItem;

import java.util.Objects;

/**
 * Represents attribute item withing {@link Bucket}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisualizationAttribute extends AttributeItem implements BucketItem {

    private static final long serialVersionUID = -5144496152695494774L;
    static final String NAME = "visualizationAttribute";

    /**
     * Creates new instance of visualization attribute for use in {@link Bucket}
     *
     * @param displayForm display form of attribute
     * @param localIdentifier local identifier of attribute
     */
    public VisualizationAttribute(final UriObjQualifier displayForm, final String localIdentifier) {
        super(displayForm, localIdentifier);
    }

    /**
     * Creates new instance of visualization attribute for use in {@link Bucket}
     *
     * @param displayForm display form of attribute
     * @param localIdentifier local identifier of attribute
     * @param alias alias of attribute
     */
    @JsonCreator
    public VisualizationAttribute(@JsonProperty("displayForm") final UriObjQualifier displayForm,
                                  @JsonProperty("localIdentifier") final String localIdentifier,
                                  @JsonProperty("alias") final String alias) {
        super(displayForm, localIdentifier, alias);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisualizationAttribute attribute = (VisualizationAttribute) o;
        return super.equals(attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
