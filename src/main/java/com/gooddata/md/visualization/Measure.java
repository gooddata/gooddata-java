/*
 * Copyright (C) 2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.visualization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.executeafm.afm.MeasureDefinition;
import com.gooddata.executeafm.afm.MeasureItem;

import java.util.Objects;

/**
 * Represents measure item within {@link Bucket}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Measure extends MeasureItem implements BucketItem {

    private static final long serialVersionUID = -6311373783004640731L;
    static final String NAME = "measure";

    private String title;

    /**
     * Creates new instance of measure for use in {@link VisualizationObject}
     * @param definition measure definition
     * @param localIdentifier local identifier
     */
    public Measure(final MeasureDefinition definition, final String localIdentifier) {
        super(definition, localIdentifier);
    }

    /**
     * Creates new instance of measure for use in {@link VisualizationObject}
     * @param definition measure definition
     * @param localIdentifier local identifier
     * @param alias alias for measure title
     * @param title default name given to measure
     * @param format format of measure to be computed
     */
    @JsonCreator
    public Measure(@JsonProperty("definition") final MeasureDefinition definition,
                   @JsonProperty("localIdentifier") final String localIdentifier,
                   @JsonProperty("alias") final String alias,
                   @JsonProperty("title") final String title,
                   @JsonProperty("format") final String format) {
        super(definition, localIdentifier, alias, format);
        this.title = title;
    }

    /**
     * @return true if measure definition has compute ratio set to true, false otherwise
     */
    @JsonIgnore
    public boolean hasComputeRatio() {
        return getDefinition() instanceof VOSimpleMeasureDefinition && ((VOSimpleMeasureDefinition) getDefinition()).hasComputeRatio();
    }

    /**
     * @return title of measure
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title of measure
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return true if measure contains {@link VOPopMeasureDefinition}, false otherwise
     */
    @JsonIgnore
    public boolean isPop() {
        return getDefinition() instanceof VOPopMeasureDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measure measure = (Measure) o;
        return super.equals(measure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
