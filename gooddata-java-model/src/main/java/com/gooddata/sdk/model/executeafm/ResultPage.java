/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.gooddata.sdk.model.executeafm.response.ExecutionResponse;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;

import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static java.util.stream.Collectors.joining;

/**
 * Represents page of {@link ExecutionResult} to be requested, using
 * {@link com.gooddata.sdk.executeafm.ExecuteAfmService#getResult(ExecutionResponse, ResultPage)}
 */
public class ResultPage {

    private final List<Integer> offsets;
    private final List<Integer> limits;

    /**
     * Creates new instance
     * @param offsets list of page offsets
     * @param limits list of page limits
     */
    public ResultPage(final List<Integer> offsets, final List<Integer> limits) {
        this.offsets = notEmpty(offsets, "offsets");
        this.limits = notEmpty(limits, "limits");
        if (offsets.size() != limits.size()) {
            throw new IllegalArgumentException("Offsets and limits can't have different size.");
        }
    }

    /**
     * @return page offsets joined and URL-encoded to be used as query parameter
     */
    public String getOffsetsQueryParam() {
        return toQueryParam(offsets);
    }

    /**
     * @return page limits joined and URL-encoded to be used as query parameter
     */
    public String getLimitsQueryParam() {
        return toQueryParam(limits);
    }

    private static String toQueryParam(final List<Integer> list) {
        return list.stream().map(String::valueOf).collect(joining("%2C"));
    }
}
