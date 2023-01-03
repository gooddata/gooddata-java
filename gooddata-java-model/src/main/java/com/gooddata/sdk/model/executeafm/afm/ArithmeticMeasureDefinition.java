/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Arithmetic measure definition representing aggregation of existing measures, for example sum of measures, difference,...
 */
@JsonRootName(PreviousPeriodMeasureDefinition.NAME)
public class ArithmeticMeasureDefinition implements MeasureDefinition {

    private static final long serialVersionUID = -2597112924341600780L;

    static final String NAME = "arithmeticMeasure";

    private final List<String> measureIdentifiers;
    private final String operator;

    /**
     * Constructor of {@link ArithmeticMeasureDefinition}
     * @param measureIdentifiers local identifiers of measures
     * @param operator operator used for aggregation, for example sum, difference, multiplication, ratio, growth
     */
    @JsonCreator
    public ArithmeticMeasureDefinition(
            @JsonProperty("measureIdentifiers") final List<String> measureIdentifiers,
            @JsonProperty("operator") final String operator) {
        this.measureIdentifiers = measureIdentifiers;
        this.operator = operator;
    }

    /**
     * no qualifiers are used, only local identifiers are used see {@link ArithmeticMeasureDefinition#getOperator()}
     * @return empty set
     */
    @Override
    public Collection<ObjQualifier> getObjQualifiers() {
        return Collections.EMPTY_SET; //has no qualifiers
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
    public List<String> getMeasureIdentifiers() {
        return measureIdentifiers;
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
