/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.Serializable;

import static com.gooddata.executeafm.afm.PopMeasureDefinition.NAME;

/**
 * Definition of so called "period over period" measure
 */
@JsonRootName(NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PopMeasureDefinition implements MeasureDefinition, Serializable {

    private static final long serialVersionUID = 1430640153994197345L;
    static final String NAME = "popMeasure";

    private final String measureIdentifier;

    private final ObjQualifier popAttribute;

    private final Integer offset;

    /**
     * Creates new definition from given measure identifier referencing another measure in {@link Afm} and given attribute qualifier (should qualify date
     * attribute)
     *
     * @param measureIdentifier
     *         measure identifier
     * @param popAttribute
     *         "period over period" date attribute
     * @param offset
     *         the number of periods the time window defined by {@code popAttribute} is offset about to the past (when value is negative) or future (when
     *         value is positive)
     */
    @JsonCreator
    public PopMeasureDefinition(@JsonProperty("measureIdentifier") final String measureIdentifier,
                                @JsonProperty("popAttribute") final ObjQualifier popAttribute,
                                @JsonProperty("offset") final Integer offset) {
        this.measureIdentifier = measureIdentifier;
        this.popAttribute = popAttribute;
        this.offset = offset;
    }

    /**
     * Creates new definition from given measure identifier referencing another measure in {@link Afm} and given attribute qualifier (should qualify date
     * attribute)
     *
     * @param measureIdentifier
     *         measure identifier
     * @param popAttribute
     *         "period over period" date attribute
     */
    public PopMeasureDefinition(final String measureIdentifier,
                                final ObjQualifier popAttribute) {
        this(measureIdentifier, popAttribute, null);
    }

    @Override
    public MeasureDefinition withObjUriQualifier(final UriObjQualifier qualifier) {
        return new PopMeasureDefinition(measureIdentifier, qualifier, offset);
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

    /**
     * Returns number of periods defined via {@link #popAttribute} time interval.
     *
     * @return positive or negative number of periods or {@code null} when offset was not defined
     */
    public Integer getOffset() {
        return offset;
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
