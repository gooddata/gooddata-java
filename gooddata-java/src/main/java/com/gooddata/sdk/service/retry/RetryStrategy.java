/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry;

import java.net.URI;

/**
 * Interface for describing retry strategy.
 */
public interface RetryStrategy {

    /**
     * Method says if retry is allowed for given parameter combination.
     * @param method HTTP method
     * @param statusCode HTTP response code
     * @param uri requested URL
     * @return {@code true} it retry is allowed
     */
    boolean retryAllowed(String method, int statusCode, URI uri);

}
