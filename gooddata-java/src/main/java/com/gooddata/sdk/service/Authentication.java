/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * User authentication strategy. See https://developer.gooddata.com/api#/reference/authentication/log-in
 */
public interface Authentication {

    /**
     * Authenticate user
     * @param endpoint          GoodData Platform's endpoint
     * @param httpClientBuilder http client builder
     * @return Http client with provided authentication means
     */
    HttpClient createHttpClient(final GoodDataEndpoint endpoint, final HttpClientBuilder httpClientBuilder);

}
