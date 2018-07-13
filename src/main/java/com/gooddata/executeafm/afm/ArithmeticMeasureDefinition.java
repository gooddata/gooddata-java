/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.gooddata.executeafm.afm.PreviousPeriodMeasureDefinition.NAME;

/**
 * Arithmetic measure definition representing aggregation of existing measures, for example sum of measures, difference,...
 */
@JsonRootName(NAME)
public class ArithmeticMeasureDefinition implements MeasureDefinition {

    static final String NAME = "arithmeticMeasure";

    private final List<String> measures;
    private final String operator;

    /**
     * Constructor of {@link ArithmeticMeasureDefinition}
     * @param measures local identifiers of measures
     * @param operator operator used for aggregation, for example sum, difference, multiplication, ratio, growth
     */
    @JsonCreator
    public ArithmeticMeasureDefinition(
            @JsonProperty("measures") final List<String> measures,
            @JsonProperty("operator") final String operator) {
        this.measures = measures;
        this.operator = operator;
    }

    @Deprecated
    @Override
    public ObjQualifier getObjQualifier() {
        throw new UnsupportedOperationException("not supported operation");
    }

    /**
     * no qualifiers are used, only local identifiers are used see {@link ArithmeticMeasureDefinition#getOperator()}
     * @return empty set
     */
    @Override
    public Collection<ObjQualifier> getObjQualifiers() {
        return Collections.EMPTY_SET; //has no qualifiers
    }

    @Deprecated
    @Override
    public MeasureDefinition withObjUriQualifier(UriObjQualifier qualifier) {
        throw new UnsupportedOperationException("not supported operation");
    }

    /**
     * no conversion is done, because this definition uses only local identifiers
     * @return this instance
     */
    @Override
    public MeasureDefinition withObjUriQualifiers(ObjQualifierConverter objQualifierConverter) {
        return this; //nothing to convert
    }

    @Override
    public boolean isAdHoc() {
        return true;
    }

    /**
     * get local identifiers of used measures
     * @return local identifiers of measure
     */
    public List<String> getMeasures() {
        return measures;
    }

    /**
     * get used operator
     * @return used operator
     */
    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
