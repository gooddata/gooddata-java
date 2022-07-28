/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.common.GoodDataException;

import java.util.concurrent.TimeUnit;

/**
 * Represents the result retrieved by polling on the REST API.
 */
public interface FutureResult<T> {

    /**
     * Checks if the result is available
     *
     * @return true if so
     * @throws GoodDataException when polling fails or the thread was interrupted
     */
    boolean isDone();

    /**
     * Wait for the result to be available and return it's value
     *
     * @return result value
     * @throws GoodDataException when polling fails or the thread was interrupted
     */
    T get();

    /**
     * Wait for the result to be available up to given time and return it's value
     *
     * @param timeout timeout value
     * @param unit    timeout unit
     * @return result value
     * @throws GoodDataException when polling fails, the timeout expires or the thread was interrupted
     */
    T get(final long timeout, final TimeUnit unit);

    /**
     * Get URI used for polling
     *
     * @return URI string
     */
    String getPollingUri();
}
