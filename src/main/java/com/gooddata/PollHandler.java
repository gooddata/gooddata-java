/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * For internal use by services employing polling.<p>
 * @param <P> polling type
 * @param <R> result type
 *
 * @see com.gooddata.FutureResult
 */
public interface PollHandler<P,R> {

    /**
     * Get URI used for polling.
     *
     * @return URI string
     */
    String getPollingUri();

    /**
     * Get class of result after polling.
     *
     * @return result class
     */
    Class<R> getResultClass();

    /**
     * Get class of the polling object.
     *
     * @return polling class
     */
    Class<P> getPollClass();

    /**
     * Returns true when the polling is done, false otherwise.
     *
     * @return true when the polling is done, false otherwise
     */
    boolean isDone();

    /**
     * Return result after polling.
     *
     * @return result after polling
     */
    R getResult();

    /**
     * Check single polling response if whole polling process should finish.
     *
     * @param response client side HTTP response
     * @return true if polling should finish, false otherwise
     * @throws IOException when there's a problem extracting data from response
     */
    boolean isFinished(ClientHttpResponse response) throws IOException;

    /**
     * Handle result of single polling request.
     *
     * @param pollResult result of polling request
     */
    void handlePollResult(P pollResult);
}
