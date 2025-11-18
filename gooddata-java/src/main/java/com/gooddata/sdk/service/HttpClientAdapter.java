/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import java.net.URI;

/**
 * Adapter interface for HTTP client operations that can work with both RestTemplate
 * and modern HTTP client implementations. Provides a unified API for REST operations.
 */
public interface HttpClientAdapter {

    /**
     * Execute HTTP request with given parameters
     *
     * @param url URL to execute request against
     * @param method HTTP method
     * @param requestCallback callback for request preparation
     * @param responseExtractor extractor for response processing
     * @param <T> response type
     * @return extracted response data
     * @throws com.gooddata.sdk.common.GoodDataRestException on HTTP errors
     */
    <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor);

    /**
     * Execute HTTP request with given URI
     *
     * @param uri URI to execute request against
     * @param method HTTP method
     * @param requestCallback callback for request preparation
     * @param responseExtractor extractor for response processing
     * @param <T> response type
     * @return extracted response data
     * @throws com.gooddata.sdk.common.GoodDataRestException on HTTP errors
     */
    <T> T execute(URI uri, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor);
}