/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.authentication;

import com.gooddata.GoodDataEndpoint;
import com.gooddata.Authentication;
import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.http.client.SimpleSSTRetrievalStrategy;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * SST authentication, see https://developer.gooddata.com/article/gooddata-token-types#Authentication
 */
public class SstAuthentication implements Authentication {

    private final String sst;

    /**
     * Create SST authentication
     * @param sst sst
     */
    public SstAuthentication(final String sst) {
        this.sst = notEmpty(sst, "sst");
    }

    @Override
    public HttpClient createHttpClient(final GoodDataEndpoint endpoint, final HttpClientBuilder builder) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new SimpleSSTRetrievalStrategy(sst);
        final HttpHost httpHost = new HttpHost(endpoint.getHostname(), endpoint.getPort(), endpoint.getProtocol());
        return new GoodDataHttpClient(httpClient, httpHost, strategy);
    }
}
