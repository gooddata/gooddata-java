/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * For internal usage by services employing polling.<p>
 * @param <P> polling type
 * @param <R> result type
 *
 * @see com.gooddata.FutureResult
 */
public interface PollHandler<P,R> {

    /**
     * Get URI used for polling
     *
     * @return URI string
     */
    String getPollingUri();

    Class<R> getResultClass();

    Class<P> getPollClass();

    boolean isDone();

    /**
     * Return result of polling
     *
     * @return result
     */
    R getResult();

    /**
     * Check if polling should finish
     *
     * @param response client side http response
     * @return true if polling should finish
     * @throws IOException
     */
    boolean isFinished(ClientHttpResponse response) throws IOException;

    void handlePollResult(P pollResult);
}
