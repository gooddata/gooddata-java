/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static com.gooddata.util.Validate.notNullState;

/**
 * Represents response on {@link com.gooddata.executeafm.Execution} request.
 * Provides the dimensions with headers and the (polling) uri to {@link com.gooddata.executeafm.result.ExecutionResult}
 * (so called dataResult).
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionResponse")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutionResponse {

    private static final String EXECUTION_RESULT_LINK = "executionResult";

    private final List<ResultDimension> dimensions;
    private final Map<String, String> links;

    /**
     * Creates new instance of given dimensions and execution result uri.
     * @param dimensions dimensions
     * @param executionResultUri execution result uri
     */
    public ExecutionResponse(final List<ResultDimension> dimensions, final String executionResultUri) {
        this(dimensions, new LinkedHashMap<>());
        links.put(EXECUTION_RESULT_LINK, notEmpty(executionResultUri, "executionResultUri"));
    }

    @JsonCreator
    private ExecutionResponse(@JsonProperty("dimensions") final List<ResultDimension> dimensions,
                              @JsonProperty("links") final Map<String, String> links) {
        this.dimensions = notNull(dimensions, "dimensions");
        this.links = notNull(links, "links");
    }

    /**
     * List of dimensions describing the result.
     * @return dimensions
     */
    public List<ResultDimension> getDimensions() {
        return dimensions;
    }

    /**
     * Map of response's links.
     * @return links
     */
    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * Uri referencing the data result location.
     * @return execution result uri or throws exception in case the link doesn't exist
     */
    @JsonIgnore
    public String getExecutionResultUri() {
        return notNullState(notNullState(links, "links").get(EXECUTION_RESULT_LINK), EXECUTION_RESULT_LINK);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ExecutionResponse that = (ExecutionResponse) o;
        return Objects.equals(dimensions, that.dimensions) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimensions, links);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
