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
import com.gooddata.sdk.model.executeafm.afm.Aggregation;
import com.gooddata.sdk.model.executeafm.afm.filter.FilterItem;
import com.gooddata.sdk.model.executeafm.afm.MeasureDefinition;
import com.gooddata.sdk.model.executeafm.afm.SimpleMeasureDefinition;

import java.util.List;

import static com.gooddata.sdk.model.md.visualization.VOSimpleMeasureDefinition.JSON_ROOT_NAME;

/**
 * Simple measure definition to be used within {@link Measure}
 *
 * @deprecated identical with {@link MeasureDefinition}, see https://github.com/gooddata/gooddata-java/issues/581
 * Let's remove it once it's removed from API.
 */
@Deprecated
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(JSON_ROOT_NAME)
public class VOSimpleMeasureDefinition extends SimpleMeasureDefinition {

    private static final long serialVersionUID = 8467311354259963694L;
    public static final String JSON_ROOT_NAME = "measureDefinition";
    /**
     * @deprecated use JSON_ROOT_NAME instead
     */
    @Deprecated
    public static final String NAME = JSON_ROOT_NAME;

    /**
     * Creates instance of simple measure definition to be used in {@link VisualizationObject}
     *
     * @see SimpleMeasureDefinition#SimpleMeasureDefinition(ObjQualifier)
     */
    public VOSimpleMeasureDefinition(ObjQualifier item) {
        super(item);
    }

    /**
     * Creates instance of simple measure definition to be used in {@link VisualizationObject}
     *
     * @param item         uri to measure
     * @param aggregation  used aggregation function
     * @param computeRatio indicates if result should be calculated in percents
     * @param filters      filters by which measure is filtered
     */
    @JsonCreator
    public VOSimpleMeasureDefinition(@JsonProperty("item") final ObjQualifier item,
                                     @JsonProperty("aggregation") final String aggregation,
                                     @JsonProperty("computeRatio") final Boolean computeRatio,
                                     @JsonProperty("filters") final List<FilterItem> filters) {
        super(item, aggregation, computeRatio, filters);
    }

    /**
     * Creates instance of simple measure definition to be used in {@link VisualizationObject}
     *
     * @see SimpleMeasureDefinition#SimpleMeasureDefinition(ObjQualifier, Aggregation, Boolean, List)
     */
    public VOSimpleMeasureDefinition(ObjQualifier item, Aggregation aggregation, Boolean computeRatio,
                                     List<FilterItem> filters) {
        super(item, aggregation, computeRatio, filters);
    }

    /**
     * Creates instance of simple measure definition to be used in {@link VisualizationObject}
     *
     * @see SimpleMeasureDefinition#SimpleMeasureDefinition(ObjQualifier, Aggregation, Boolean, FilterItem...)
     */
    public VOSimpleMeasureDefinition(ObjQualifier item, Aggregation aggregation, Boolean computeRatio,
                                     FilterItem... filters) {
        super(item, aggregation, computeRatio, filters);
    }
}
