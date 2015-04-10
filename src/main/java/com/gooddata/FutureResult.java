/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

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
    public boolean isDone();

    /**
     * Wait for the result to be available and return it's value
     *
     * @return result value
     * @throws GoodDataException when polling fails or the thread was interrupted
     */
    public T get();

    /**
     * Wait for the result to be available up to given time and return it's value
     *
     * @param timeout timeout value
     * @param unit    timeout unit
     * @return result value
     * @throws GoodDataException when polling fails, the timeout expires or the thread was interrupted
     */
    public T get(final long timeout, final TimeUnit unit);

}
