/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static java.util.Arrays.asList;

/**
 * Native total definition
 */
public class NativeTotalItem {
    private final String measureIdentifier;
    private final List<String> attributeIdentifiers;

    /**
     * Native total definition
     * @param measureIdentifier measure on which is total defined
     * @param attributeIdentifiers subset of internal attribute identifiers in AFM defining total placement
     */
    @JsonCreator
    public NativeTotalItem(
            @JsonProperty("measureIdentifier") final String measureIdentifier,
            @JsonProperty("attributeIdentifiers") final List<String> attributeIdentifiers) {
        this.measureIdentifier = notEmpty(measureIdentifier, "measureIdentifier");
        this.attributeIdentifiers = attributeIdentifiers == null ? Collections.emptyList() : attributeIdentifiers;
    }

    /**
     * Native total definition
     * @param measureIdentifier measure on which is total defined
     * @param attributeIdentifiers subset of internal attribute identifiers in AFM defining total placement
     */
    public NativeTotalItem(final String measureIdentifier, final String... attributeIdentifiers) {
        this(measureIdentifier, asList(attributeIdentifiers));
    }

    /**
     * internal identifier of measure in AFM, on which is total defined
     * @return measure
     */
    public String getMeasureIdentifier() {
        return measureIdentifier;
    }

    /**
     * subset of internal attribute identifiers in AFM defining total placement
     * @return list of identifiers (never null)
     */
    public List<String> getAttributeIdentifiers() {
        return attributeIdentifiers;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
