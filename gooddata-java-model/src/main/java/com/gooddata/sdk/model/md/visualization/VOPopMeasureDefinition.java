/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.afm.PopMeasureDefinition;

import static com.gooddata.sdk.model.md.visualization.VOPopMeasureDefinition.JSON_ROOT_NAME;

/**
 * Period over Period measure definition to be used within {@link Measure}
 *
 * @deprecated identical with {@link PopMeasureDefinition}, see https://github.com/gooddata/gooddata-java/issues/581
 * Let's remove it once it's removed from API.
 */
@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(JSON_ROOT_NAME)
public class VOPopMeasureDefinition extends PopMeasureDefinition {

    private static final long serialVersionUID = -2727004914980057124L;
    public static final String JSON_ROOT_NAME = "popMeasureDefinition";
    /**
     * @deprecated use JSON_ROOT_NAME instead
     */
    @Deprecated
    public static final String NAME = JSON_ROOT_NAME;

    /**
     * Creates instance of Period over Period measure definition to be used in {@link VisualizationObject}
     *
     * @param measureIdentifier reference to local identifier of {@link VOSimpleMeasureDefinition} over which is PoP calculated
     * @param popAttribute      uri to attribute used for PoP
     */
    @JsonCreator
    public VOPopMeasureDefinition(@JsonProperty("measureIdentifier") final String measureIdentifier,
                                  @JsonProperty("popAttribute") final ObjQualifier popAttribute) {
        super(measureIdentifier, popAttribute);
    }

}
