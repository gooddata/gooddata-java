/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.executeafm.afm.PopMeasureDefinition.NAME;

/**
 * Definition of so called "period over period" measure
 */
@JsonRootName(NAME)
public class PopMeasureDefinition implements MeasureDefinition {

    static final String NAME = "popMeasure";

    private final String measureIdentifier;

    private final ObjQualifier popAttribute;

    /**
     * Creates new definition from given measure identifier referencing another measure in {@link Afm} and given
     * attribute qualifier (should qualify date attribute)
     * @param measureIdentifier measure identifier
     * @param popAttribute "period over period" date attribute
     */
    public PopMeasureDefinition(@JsonProperty("measureIdentifier") final String measureIdentifier,
                                @JsonProperty("popAttribute") final ObjQualifier popAttribute) {
        this.measureIdentifier = measureIdentifier;
        this.popAttribute = popAttribute;
    }

    @Override
    public MeasureDefinition withObjUriQualifier(final UriObjQualifier qualifier) {
        return new PopMeasureDefinition(measureIdentifier, qualifier);
    }

    /**
     * @return always true (PopMeasure is always ad-hoc)
     */
    @Override
    public boolean isAdHoc() {
        return true;
    }

    public String getMeasureIdentifier() {
        return measureIdentifier;
    }

    public ObjQualifier getPopAttribute() {
        return popAttribute;
    }

    @Override
    public ObjQualifier getObjQualifier() {
        return getPopAttribute();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
