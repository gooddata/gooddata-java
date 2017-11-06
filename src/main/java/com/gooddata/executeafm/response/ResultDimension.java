/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Describes single dimension of the {@link com.gooddata.executeafm.result.ExecutionResult}.
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
