/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import java.util.concurrent.TimeUnit;

import static com.gooddata.Validate.notNull;

/**
 * Represents the result retrieved by polling on the REST API.
 */
public final class FutureResult<T> {

    private final AbstractService service;

    private final PollHandler<T> handler;

    /**
     * Creates a new instance of the result to be eventually retrieved by polling on the REST API.<p>
     * For internal usage by services employing polling.
     *
     * @param service this service
     * @param handler poll handler
     */
    public FutureResult(final AbstractService service, final PollHandler<T> handler) {
        this.service = notNull(service, "service");
        this.handler = notNull(handler, "handler");
    }

    /**
     * Checks if the result is available
     *
     * @return true if so
     * @throws GoodDataException when polling fails or the thread was interrupted
     */
    public boolean isDone() {
        return handler.isDone() || service.pollOnce(handler);
    }

    /**
     * Wait for the result to be available and return it's value
     *
     * @return result value
     * @throws GoodDataException when polling fails or the thread was interrupted
     */
    public T get() {
        return get(0, null);
    }

    /**
     * Wait for the result to be available up to given time and return it's value
     *
     * @param timeout timeout value
     * @param unit    timeout unit
     * @return result value
     * @throws GoodDataException when polling fails, the timeout expires or the thread was interrupted
     */
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
    public String getPollingUri() {
        return handler.getPollingUri();
    }
}
