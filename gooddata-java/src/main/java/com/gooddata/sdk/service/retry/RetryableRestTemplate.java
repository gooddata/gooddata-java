/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import com.gooddata.GoodDataRestException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * REST template with retry ability. Its behavior is described by given strategy and retry template.
 */
public class RetryableRestTemplate extends RestTemplate {

    private final RetryTemplate retryTemplate;
    private final RetryStrategy retryStrategy;

    /**
     * Create a new instance of the {@link RetryableRestTemplate}.
     * @param requestFactory HTTP request factory to use
     * @param retryTemplate retry template
     * @param retryStrategy retry strategy
     */
    public RetryableRestTemplate(ClientHttpRequestFactory requestFactory, RetryTemplate retryTemplate, RetryStrategy retryStrategy) {
        super(requestFactory);
        notNull(retryTemplate);
        this.retryTemplate = retryTemplate;
        this.retryStrategy = retryStrategy;
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
                              ResponseExtractor<T> responseExtractor) throws RestClientException {
        return retryTemplate.execute(context -> {
            System.out.println("Retrying " + context.getRetryCount() + " " + method + " ");
            try {
                return super.doExecute(url, method, requestCallback, responseExtractor);
            } catch (GoodDataRestException e) {
                if (!retryStrategy.retryAllowed(method.toString(), e.getStatusCode(), url)) {
                    context.setExhaustedOnly();
                }
                throw e;
            }
        });
    }

    /**
     * Creates new retryable REST template.
     * @param retrySettings retry settings
     * @param factory request factory
     * @return retryable rest template
     */
    public static RestTemplate create(RetrySettings retrySettings, ClientHttpRequestFactory factory) {
        final RetryTemplate retryTemplate = new RetryTemplate();

        if (retrySettings.getRetryCount() != null) {
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(retrySettings.getRetryCount()));
        }

        if (retrySettings.getRetryInitialInterval() != null) {
            if (retrySettings.getRetryMultiplier() != null) {
                final ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
                exponentialBackOffPolicy.setInitialInterval(retrySettings.getRetryInitialInterval());
                exponentialBackOffPolicy.setMultiplier(retrySettings.getRetryMultiplier());
                exponentialBackOffPolicy.setMaxInterval(retrySettings.getRetryMaxInterval());
                retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
            } else {
                final FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
                backOffPolicy.setBackOffPeriod(retrySettings.getRetryInitialInterval());
                retryTemplate.setBackOffPolicy(backOffPolicy);
            }
        }

        return new RetryableRestTemplate(factory, retryTemplate, new GetServerErrorRetryStrategy());
    }
}
