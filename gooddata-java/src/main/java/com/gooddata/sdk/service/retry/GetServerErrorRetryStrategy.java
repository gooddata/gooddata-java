/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Allows retry for GET method and some HTTP 5XX states mentioned in {@link GetServerErrorRetryStrategy#RETRYABLE_STATES}.
 */
public class GetServerErrorRetryStrategy implements RetryStrategy {

    public static final Collection<Integer> RETRYABLE_STATES = Collections.unmodifiableCollection(asList(500, 502, 503, 504, 507));
    public static final Collection<String> RETRYABLE_METHODS = Collections.unmodifiableCollection(asList("GET"));

    @Override
    public boolean retryAllowed(String method, int statusCode, URI uri) {
        return RETRYABLE_STATES.contains(statusCode) && RETRYABLE_METHODS.contains(method);
    }

}

