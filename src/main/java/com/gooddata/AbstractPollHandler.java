/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.client.ClientHttpResponse;

import static com.gooddata.util.Validate.notNull;

/**
 * For internal use by services employing polling.<p>
 * Implementing classes should override {@link #isFinished(ClientHttpResponse)} method and
 * may override {@link #onFinish()} and {@link #handlePollResult(Object)} methods.
 *
 * @param <P> polling type
 * @param <R> result type
 *
 * @see com.gooddata.FutureResult
 */
public abstract class AbstractPollHandler<P,R> extends AbstractPollHandlerBase<P,R> {

    private final String pollingUri;

    /**
     * Creates a new instance of polling handler
     *  @param pollingUri  URI for polling
     *  @param pollClass class of the polling object (or {@link Void})
     *  @param resultClass class of the result (or {@link Void})
     */
    public AbstractPollHandler(final String pollingUri, final Class<P> pollClass, Class<R> resultClass) {
        super(pollClass, resultClass);
        this.pollingUri = notNull(pollingUri, "pollingUri");
    }

    @Override
    public final String getPollingUri() {
        return pollingUri;
    }

}
