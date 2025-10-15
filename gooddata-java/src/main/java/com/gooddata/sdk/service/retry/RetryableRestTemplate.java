/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import com.gooddata.sdk.common.GoodDataRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            try {
                return super.doExecute(url, method, requestCallback, responseExtractor);
            } catch (GoodDataRestException e) {
                if (!retryStrategy.retryAllowed(method.toString(), e.getStatusCode(), url)) {
                    context.setExhaustedOnly();
                } else {
                    final int retryCount = context.getRetryCount();
                    logger.info("{}call of {} {} failed, HTTP {} and will be retried, {} ", retryCount == 0 ? "" : retryCount + " ", method, url, e.getStatusCode(), e.getMessage());
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

