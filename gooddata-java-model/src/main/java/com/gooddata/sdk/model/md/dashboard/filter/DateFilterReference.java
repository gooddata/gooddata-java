/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Reference for date filter for ignoring particular filter in {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 * Is not standalone metadata object - must be part of {@link com.gooddata.sdk.model.md.dashboard.Kpi}.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(DateFilterReference.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateFilterReference implements FilterReference {

    private static final long serialVersionUID = 6016252592161989340L;

    static final String NAME = "dateFilterReference";

    private final String datasetUri;

    /**
     * Constructor.
     * @param datasetUri date dataset URI
     */
    @JsonCreator
    public DateFilterReference(@JsonProperty("dataSet") final String datasetUri) {
        this.datasetUri = notEmpty(datasetUri, "datasetUri");
    }

    /**
     * @return date dataset URI of the filter
     */
    @JsonProperty("dataSet")
    public String getDatasetUri() {
        return datasetUri;
    }
}
