/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Describes single dimension of the {@link ExecutionResult}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDimension {
    private final List<Header> headers;

    /**
     * Creates the result dimension of given headers
     * @param headers headers
     */
    @JsonCreator
    public ResultDimension(@JsonProperty("headers") final List<Header> headers) {
        this.headers = headers;
    }

    /**
     * Creates the result dimension of given headers
     * @param headers headers
     */
    public ResultDimension(final Header... headers) {
        this(asList(headers));
    }

    /**
     * @return dimension headers
     */
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
