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
 * may override {@link #onFinish()} and {@link #handlePollResult(Object)} methods.
 *
 * @param <P> polling type
 * @param <R> result type
 *
 * @see com.gooddata.FutureResult
 */
public class PollHandler<P,R> {

    private final String pollingUri;

    private final Class<P> pollClass;
    private final Class<R> resultClass;

    private boolean done = false;

    private R result;

    /**
     * Creates a new instance of polling handler
     *  @param pollingUri  URI for polling
     *  @param pollAndResultClass class of the polling object and result  (or {@link Void})
     */
    @SuppressWarnings("unchecked")
    public PollHandler(final String pollingUri, final Class pollAndResultClass) {
        this(pollingUri, pollAndResultClass, pollAndResultClass);
    }

    /**
     * Creates a new instance of polling handler
     *  @param pollingUri  URI for polling
     *  @param pollClass class of the polling object (or {@link Void})
     *  @param resultClass class of the result (or {@link Void})
     */
    public PollHandler(final String pollingUri, final Class<P> pollClass, Class<R> resultClass) {
        this.pollingUri = notNull(pollingUri, "pollingUri");
        this.pollClass = notNull(pollClass, "pollClass");
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

    final Class<R> getResultClass() {
        return resultClass;
    }

    final Class<P> getPollClass() {
        return pollClass;
    }

    protected PollHandler<P,R> setResult(R result) {
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
    protected final R getResult() {
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

    protected void handlePollResult(P pollResult) {
        if (resultClass.equals(pollClass)) {
            setResult(resultClass.cast(pollResult));
        } else {
            throw new IllegalStateException("Please override handlePollResult method when you want different type of polling and result class");
        }
    }
}
