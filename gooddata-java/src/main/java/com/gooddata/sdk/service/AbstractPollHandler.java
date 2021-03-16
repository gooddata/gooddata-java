/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.springframework.http.client.ClientHttpResponse;

import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * For internal use by services employing polling.<p>
 * Implementing classes should override {@link #isFinished(ClientHttpResponse)} method and
 * may override {@link #onFinish()} and {@link #handlePollResult(Object)} methods.
 *
 * @param <P> polling type
 * @param <R> result type
 *
 * @see FutureResult
 */
public abstract class AbstractPollHandler<P,R> extends AbstractPollHandlerBase<P,R> {

    private URI pollingUri;

    /**
     * Creates a new instance of polling handler
     * @param pollingUri  URI for polling
     * @param pollClass class of the polling object (or {@link Void})
     * @param resultClass class of the result (or {@link Void})
     */
    public AbstractPollHandler(final String pollingUri, final Class<P> pollClass, Class<R> resultClass) {
        super(pollClass, resultClass);
        setPollingUri(pollingUri);
    }

    @Override
    public final String getPollingUri() {
        return pollingUri.toString();
    }

    @Override
    public final URI getPolling() {
        return pollingUri;
    }

    protected void setPollingUri(final String pollingUri) {
        this.pollingUri = URI.create(notNull(pollingUri, "pollingUri"));
    }
}
