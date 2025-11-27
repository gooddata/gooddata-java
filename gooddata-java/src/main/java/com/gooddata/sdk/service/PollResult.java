/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import java.util.concurrent.TimeUnit;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Represents the result retrieved by polling on the REST API.
 */
public final class PollResult<T> implements FutureResult<T> {

    private final AbstractService service;

    private final PollHandler<?, T> handler;

    /**
     * Creates a new instance of the result to be eventually retrieved by polling on the REST API.<p>
     * For internal use by services employing polling.
     *
     * @param service this service
     * @param handler poll handler
     */
    public PollResult(final AbstractService service, final PollHandler<?, T> handler) {
        this.service = notNull(service, "service");
        this.handler = notNull(handler, "handler");
    }

    @Override
    public boolean isDone() {
        return handler.isDone() || service.pollOnce(handler);
    }

    @Override
    public T get() {
        return get(0, null);
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) {
        if (handler.isDone()) {
            return handler.getResult();
        }
        return service.poll(handler, timeout, unit);
    }

    /**
     * Get URI used for polling
     *
     * @return URI string
     */
    @Override
    public String getPollingUri() {
        return handler.getPollingUri();
    }
}
