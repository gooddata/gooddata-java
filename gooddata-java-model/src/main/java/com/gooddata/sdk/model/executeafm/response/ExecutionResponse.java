/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static org.apache.commons.lang3.ArrayUtils.toObject;

/**
 * Represents response on {@link Execution} request.
 * Provides the dimensions with headers and the (polling) uri to {@link ExecutionResult}
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
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
