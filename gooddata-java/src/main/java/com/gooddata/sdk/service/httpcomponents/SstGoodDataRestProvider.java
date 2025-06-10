/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.http.client.SimpleSSTRetrievalStrategy;
import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;

import static com.gooddata.sdk.common.util.Validate.notNull;

public final class SstGoodDataRestProvider extends SingleEndpointGoodDataRestProvider {

    private final String sst;

    /**
     * Create SST REST provider
     * @param endpoint endpoint of GoodData API
     * @param settings settings
     * @param sst super secure token
     */
    public SstGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings, final String sst) {
        super(endpoint, settings);
        this.sst = sst;
    }


    public static GoodDataHttpClient createGoodDataHttpClient(HttpClientBuilder builder, GoodDataEndpoint endpoint, String sst) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");
        notNull(sst, "sst");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new SimpleSSTRetrievalStrategy(sst);
        final HttpHost httpHost = new HttpHost(endpoint.getProtocol(), endpoint.getHostname(), endpoint.getPort());
        return new GoodDataHttpClient(httpClient, httpHost, strategy);
    }


}
