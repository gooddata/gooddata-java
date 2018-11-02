/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm;

import com.gooddata.executeafm.response.ExecutionResponse;
import com.gooddata.executeafm.result.ExecutionResult;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import static com.gooddata.util.Validate.notEmpty;
import static java.util.stream.Collectors.joining;

/**
 * Represents page of {@link ExecutionResult} to be requested, using
 * {@link com.gooddata.executeafm.ExecuteAfmService#getResult(ExecutionResponse, ResultPage)}
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
    String getOffsetsQueryParam() {
        return toQueryParam(offsets);
    }

    /**
     * @return page limits joined and URL-encoded to be used as query parameter
     */
    String getLimitsQueryParam() {
        return toQueryParam(limits);
    }

    private static String toQueryParam(final List<Integer> list) {
        try {
            return UriUtils.encode(list.stream().map(String::valueOf).collect(joining(",")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Missing UTF-8 charset");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ResultPage that = (ResultPage) o;
        return Objects.equals(offsets, that.offsets) &&
                Objects.equals(limits, that.limits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offsets, limits);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
