/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * RestTemplate-based implementation of HttpClientAdapter.
 * Provides backward compatibility while allowing future migration to RestClient.
 */
public class RestTemplateHttpClientAdapter implements HttpClientAdapter {

    private final RestTemplate restTemplate;

    public RestTemplateHttpClientAdapter(RestTemplate restTemplate) {
        this.restTemplate = notNull(restTemplate, "restTemplate");
    }

    @Override
    public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        return restTemplate.execute(url, method, requestCallback, responseExtractor);
    }

    @Override
    public <T> T execute(URI uri, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
        return restTemplate.execute(uri, method, requestCallback, responseExtractor);
    }

    /**
     * Get the underlying RestTemplate for compatibility
     * @return RestTemplate instance
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}