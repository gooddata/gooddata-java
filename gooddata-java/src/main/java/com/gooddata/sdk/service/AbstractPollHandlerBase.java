/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * For internal use by services employing polling.<p>
 *
 * @param <P> polling type
 * @param <R> result type
 *
 * @see FutureResult
 */
public abstract class AbstractPollHandlerBase<P, R> implements PollHandler<P, R> {
    protected final Class<P> pollClass;
    protected final Class<R> resultClass;
    private boolean done = false;
    private R result;

    protected AbstractPollHandlerBase(Class<P> pollClass, Class<R> resultClass) {
        this.pollClass = notNull(pollClass, "pollClass");
        this.resultClass = notNull(resultClass, "resultClass");
    }

    @Override
    public final Class<R> getResultClass() {
        return resultClass;
    }

    @Override
    public final Class<P> getPollClass() {
        return pollClass;
    }

    protected PollHandler<P, R> setResult(R result) {
        this.result = result;
        this.done = true;
        onFinish();
        return this;
    }

    @Override
    public final boolean isDone() {
        return done;
    }

    @Override
    public final R getResult() {
        return result;
    }

    @Override
    public boolean isFinished(final ClientHttpResponse response) throws IOException {
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    /**
     * Method called after polling is successfully finished (default no-op)
     */
    protected void onFinish() {
    }
}

