/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import java.util.Objects;

import static org.springframework.util.Assert.isTrue;

/**
 * Contains settings for HTTP requests retry.
 */
public class RetrySettings {

    public Integer DEFAULT_RETRY_COUNT =  6;
    private Long DEFAULT_RETRY_INITIAL_INTERVAL = 1 * 1000l;  // 1s
    private Long DEFAULT_RETRY_MAX_INTERVAL = 1 * 60 * 1000l; // 1min
    private Double DEFAULT_RETRY_MULTIPLIER = 2d;

    private Integer retryCount = DEFAULT_RETRY_COUNT;
    private Long retryInitialInterval = DEFAULT_RETRY_INITIAL_INTERVAL;
    private Long retryMaxInterval = DEFAULT_RETRY_MAX_INTERVAL;
    private Double retryMultiplier = DEFAULT_RETRY_MULTIPLIER;

    /**
     * Total retry count. Should be > 0. No retry if not set.
     * @return retry count
     */
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        isTrue(retryCount == null || retryCount > 0, "retryCount hast to be greater than 0");
        this.retryCount = retryCount;
    }

    /**
     *
     * @return retry initial interval
     */
    public Long getRetryInitialInterval() {
        return retryInitialInterval;
    }

    public void setRetryInitialInterval(Long retryInitialInterval) {
        isTrue(retryInitialInterval > 0, "retryInitialInterval has to be greater than 0");
        this.retryInitialInterval = retryInitialInterval;
    }

    /**
     *
     * @return maximum retry interval
     */
    public Long getRetryMaxInterval() {
        return retryMaxInterval;
    }

    public void setRetryMaxInterval(Long retryMaxInterval) {
        isTrue(retryMaxInterval > 0, "retryMaxInterval has to be greater than 0");
        this.retryMaxInterval = retryMaxInterval;
    }

    /**
     * If set, exponential strategy is used. Every next retry interval will be computed as previos interval multiplied
     * by this number.
     * @return retry multiplier
     */
    public Double getRetryMultiplier() {
        return retryMultiplier;
    }

    public void setRetryMultiplier(Double retryMultiplier) {
        isTrue(retryMultiplier > 1.0, "retryMultiplier has to be greater than 1.0");
        this.retryMultiplier = retryMultiplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RetrySettings that = (RetrySettings) o;
        return Objects.equals(retryCount, that.retryCount) &&
                Objects.equals(retryInitialInterval, that.retryInitialInterval) &&
                Objects.equals(retryMaxInterval, that.retryMaxInterval) &&
                Objects.equals(retryMultiplier, that.retryMultiplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retryCount, retryInitialInterval, retryMaxInterval, retryMultiplier);
    }
}

