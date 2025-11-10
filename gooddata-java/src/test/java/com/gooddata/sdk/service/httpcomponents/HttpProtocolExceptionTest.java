/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import org.apache.hc.core5.http.HttpException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpProtocolExceptionTest {

    @Test
    void shouldPreserveHttpException() {
        final HttpException cause = new HttpException("Protocol error");
        final HttpProtocolException exception = new HttpProtocolException("Detailed message", cause);

        assertNotNull(exception.getHttpException());
        assertSame(cause, exception.getHttpException());
        assertSame(cause, exception.getCause());
        assertEquals("Detailed message", exception.getMessage());
    }

    @Test
    void shouldIncludeMessageInException() {
        final HttpException cause = new HttpException("Original protocol error");
        final HttpProtocolException exception = new HttpProtocolException(
            "HTTP protocol error during request execution: Original protocol error [target=https://example.com, request=GET /api]",
            cause
        );

        assertTrue(exception.getMessage().contains("HTTP protocol error"));
        assertTrue(exception.getMessage().contains("Original protocol error"));
        assertTrue(exception.getMessage().contains("https://example.com"));
    }

    @Test
    void shouldBeInstanceOfIOException() {
        final HttpException cause = new HttpException("Test");
        final HttpProtocolException exception = new HttpProtocolException("Test message", cause);

        assertTrue(exception instanceof java.io.IOException);
    }

    @Test
    void shouldAllowStackTraceCapture() {
        final HttpException cause = new HttpException("Test error");
        final HttpProtocolException exception = new HttpProtocolException("Wrapper message", cause);

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    void shouldPreserveCauseChain() {
        final HttpException cause = new HttpException("Root cause");
        final HttpProtocolException exception = new HttpProtocolException("Intermediate", cause);

        assertEquals("Root cause", exception.getCause().getMessage());
        assertEquals("Root cause", exception.getHttpException().getMessage());
    }
}

