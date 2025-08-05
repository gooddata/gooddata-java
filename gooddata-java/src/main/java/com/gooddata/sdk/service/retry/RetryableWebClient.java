/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import com.gooddata.sdk.common.GoodDataRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.function.Predicate;

/**
 * Function: execute
 * File: RetryableWebClient.java
 * 
 * REST WebClient with retry support for GoodData SDK.
 */
public class RetryableWebClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WebClient webClient;
    private final RetrySettings retrySettings;
    private final RetryStrategy retryStrategy;

    /**
     * Creates a new instance of RetryableWebClient.
     * @param webClient underlying reactive WebClient
     * @param retrySettings configuration for retries
     * @param retryStrategy retry allow/deny logic
     */
    public RetryableWebClient(WebClient webClient, RetrySettings retrySettings, RetryStrategy retryStrategy) {
        this.webClient = webClient;
        this.retrySettings = retrySettings;
        this.retryStrategy = retryStrategy;
    }

    /**
     * Execute an HTTP call with retries.
     * @param uri request URI
     * @param method HTTP method
     * @param responseType expected response type
     * @return Mono of response
     */
    public <T> Mono<T> execute(URI uri, HttpMethod method, Class<T> responseType) {
        return webClient.method(method)
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(
                    Retry.backoff(retrySettings.getRetryCount(),
                                  Duration.ofMillis(retrySettings.getRetryInitialInterval()))
                        .filter(retryOnException(uri, method))
                        .maxBackoff(Duration.ofMillis(retrySettings.getRetryMaxInterval()))
                )
                .onErrorMap(WebClientResponseException.class, e -> {
                    logger.warn("HTTP {} for {} {}: {}", e.getStatusCode(), method, uri, e.getMessage());
                    return new GoodDataRestException(
                        e.getStatusCode().value(),
                        e.getStatusText(),
                        e.getResponseBodyAsString(),
                        uri.toString(),
                        "WebClient"
                    );
                });
    }

    /**
     * Determines whether the exception should be retried based on status code and strategy.
     * @param uri request URI
     * @param method HTTP method
     * @return predicate for retry filter
     */
    private Predicate<Throwable> retryOnException(URI uri, HttpMethod method) {
        return throwable -> {
            if (throwable instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) throwable;
                return retryStrategy.retryAllowed(
                    method.name(),
                    wcre.getStatusCode().value(),
                    uri
                );
            }
            return false;
        };
    }
}
