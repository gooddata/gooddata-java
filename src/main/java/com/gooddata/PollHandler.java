/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.gooddata.Validate.notNull;

/**
 * For internal usage by services employing polling.<p>
 * Implementing classes should override {@link #isFinished(ClientHttpResponse)} method and
 * may override {@link #onFinish()} method.
 *
 * @see FutureResult
 */
public class PollHandler<T> {

    private final String pollingUri;

    private final Class<T> resultClass;

    private boolean done = false;

    private T result;

    /**
     * Creates a new instance of polling handler
     *
     * @param pollingUri  URI for polling
     * @param resultClass class of the result (or {@link Void})
     */
    public PollHandler(final String pollingUri, final Class<T> resultClass) {
        this.pollingUri = notNull(pollingUri, "pollingUri");
        this.resultClass = notNull(resultClass, "resultClass");
    }

    /**
     * Get URI used for polling
     *
     * @return URI string
     */
    final String getPollingUri() {
        return pollingUri;
    }

    final Class<T> getResultClass() {
        return resultClass;
    }

    final PollHandler<T> setResult(T result) {
        this.result = result;
        this.done = true;
        onFinish();
        return this;
    }

    final boolean isDone() {
        return done;
    }

    /**
     * Return result of polling
     *
     * @return result
     */
    protected final T getResult() {
        return result;
    }

    /**
     * Check if polling should finish
     *
     * @param response client side http response
     * @return true if polling should finish
     * @throws IOException
     */
    protected boolean isFinished(final ClientHttpResponse response) throws IOException {
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    /**
     * Method called after polling is successfully finished (default no-op)
     */
    protected void onFinish() {
    }
}
