/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.http.client.GoodDataHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GoodDataHttpClientAdapter}.
 */
class GoodDataHttpClientAdapterTest {

    @Mock
    private GoodDataHttpClient mockGoodDataHttpClient;

    @Mock
    private ClassicHttpRequest mockRequest;

    @Mock
    private ClassicHttpResponse mockResponse;

    @Mock
    private HttpContext mockContext;

    @Mock
    private HttpClientResponseHandler<String> mockResponseHandler;

    private GoodDataHttpClientAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new GoodDataHttpClientAdapter(mockGoodDataHttpClient);
    }

    @Test
    void shouldRejectNullRequestInExecuteWithContext() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.execute(null, mockContext)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullRequestInExecuteWithoutContext() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.execute((ClassicHttpRequest) null)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullRequestInExecuteWithResponseHandler() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.execute(null, mockContext, mockResponseHandler)
        );
        assertEquals("Request cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullResponseHandlerInExecute() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> adapter.execute(mockRequest, mockContext, null)
        );
        assertEquals("Response handler cannot be null", exception.getMessage());
    }

    @Test
    void shouldAllowNullContextInExecute() throws IOException {
        when(mockGoodDataHttpClient.execute(isNull(), eq(mockRequest), isNull()))
            .thenReturn(mockResponse);

        ClassicHttpResponse result = adapter.execute(mockRequest, (HttpContext) null);

        assertNotNull(result);
        verify(mockGoodDataHttpClient).execute(isNull(), eq(mockRequest), isNull());
    }

    @Test
    void shouldDelegateExecuteToGoodDataHttpClient() throws IOException {
        when(mockGoodDataHttpClient.execute(isNull(), eq(mockRequest), eq(mockContext)))
            .thenReturn(mockResponse);

        ClassicHttpResponse result = adapter.execute(mockRequest, mockContext);

        assertSame(mockResponse, result);
        verify(mockGoodDataHttpClient).execute(isNull(), eq(mockRequest), eq(mockContext));
    }

    @Test
    void shouldWrapHttpExceptionInHttpProtocolException() throws IOException, HttpException {
        HttpException httpException = new HttpException("Protocol error");
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getRequestUri()).thenReturn("/api/test");
        
        when(mockGoodDataHttpClient.execute(isNull(), eq(mockRequest), eq(mockContext), eq(mockResponseHandler)))
            .thenThrow(httpException);

        HttpProtocolException exception = assertThrows(
            HttpProtocolException.class,
            () -> adapter.execute(mockRequest, mockContext, mockResponseHandler)
        );

        assertTrue(exception.getMessage().contains("Protocol error"));
        assertTrue(exception.getMessage().contains("GET /api/test"));
        assertSame(httpException, exception.getHttpException());
    }

    @Test
    void shouldIncludeTargetInErrorMessage() throws IOException, HttpException {
        HttpException httpException = new HttpException("Connection error");
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getRequestUri()).thenReturn("/gdc/account/login");
        
        when(mockGoodDataHttpClient.execute(isNull(), eq(mockRequest), isNull(), eq(mockResponseHandler)))
            .thenThrow(httpException);

        HttpProtocolException exception = assertThrows(
            HttpProtocolException.class,
            () -> adapter.execute(mockRequest, mockResponseHandler)
        );

        assertTrue(exception.getMessage().contains("Connection error"));
        assertTrue(exception.getMessage().contains("no-target-specified"));
        assertTrue(exception.getMessage().contains("POST /gdc/account/login"));
    }

    @Test
    void shouldPassNullTargetToUnderlyingClient() throws IOException {
        when(mockGoodDataHttpClient.execute(isNull(), eq(mockRequest)))
            .thenReturn(mockResponse);

        adapter.execute(mockRequest);

        // Verify that null is passed as target (standard HttpClient 5.x behavior)
        verify(mockGoodDataHttpClient).execute(isNull(), eq(mockRequest));
    }
}

