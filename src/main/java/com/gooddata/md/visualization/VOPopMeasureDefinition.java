/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.gooddata.executeafm.ObjQualifier;

import static com.gooddata.md.visualization.VOPopMeasureDefinition.NAME;

/**
 * Period over Period measure definition to be used within {@link Measure}
 * @deprecated identical with {@link com.gooddata.executeafm.afm.PopMeasureDefinition}, see https://github.com/gooddata/gooddata-java/issues/581
 */
@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(NAME)
public class VOPopMeasureDefinition extends com.gooddata.executeafm.afm.PopMeasureDefinition {

    private static final long serialVersionUID = -2727004914980057124L;
    public static final String NAME = "popMeasureDefinition";

    /**
     * Creates instance of Period over Period measure definition to be used in {@link VisualizationObject}
     * @param measureIdentifier reference to local identifier of {@link VOSimpleMeasureDefinition} over which is PoP calculated
     * @param popAttribute uri to attribute used for PoP
     */
    @JsonCreator
    public VOPopMeasureDefinition(@JsonProperty("measureIdentifier") final String measureIdentifier,
                                  @JsonProperty("popAttribute") final ObjQualifier popAttribute) {
        super(measureIdentifier, popAttribute);
    }
}
