/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import org.apache.hc.core5.http.HttpException;

import java.io.IOException;

/**
 * Exception thrown when an HTTP protocol error occurs during request execution.
 * 
 * <p>This exception wraps {@link HttpException} to maintain compatibility with
 * {@link org.apache.hc.client5.http.classic.HttpClient}'s signature which only
 * allows {@link IOException}, while preserving the original exception type for
 * debugging and error handling.
 * 
 * @since 4.0.4
 */
public class HttpProtocolException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new HTTP protocol exception with the specified detail message
     * and cause.
     *
     * @param message the detail message describing the protocol error
     * @param cause the underlying {@link HttpException} that caused this exception
     */
    public HttpProtocolException(String message, HttpException cause) {
        super(message, cause);
    }

    /**
     * Returns the underlying {@link HttpException} that caused this exception.
     *
     * @return the HTTP exception, never null
     */
    public HttpException getHttpException() {
        return (HttpException) getCause();
    }

    /**
     * Returns the original HTTP exception cause.
     * 
     * <p>This is an alias for {@link #getHttpException()} for clarity.
     *
     * @return the HTTP exception cause
     */
    @Override
    public synchronized HttpException getCause() {
        return (HttpException) super.getCause();
    }
}

