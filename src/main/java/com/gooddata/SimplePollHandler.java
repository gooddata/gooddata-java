package com.gooddata;

/**
 * For internal usage by services employing polling.<p>
 * A simple poll handler using same type for polling and result.
 *
 * @param <T> polling and result type
 *
 * @see com.gooddata.FutureResult
 */
public class SimplePollHandler<T> extends AbstractPollHandler<T, T> {
    /**
     * Creates a new instance of polling handler
     *
     * @param pollingUri         URI for polling
     * @param pollAndResultClass class of the polling object and result  (or {@link Void})
     */
    @SuppressWarnings("unchecked")
    public SimplePollHandler(String pollingUri, Class pollAndResultClass) {
        super(pollingUri, pollAndResultClass, pollAndResultClass);
    }

    @Override
    public void handlePollResult(T pollResult) {
        setResult(pollResult);
    }
}
