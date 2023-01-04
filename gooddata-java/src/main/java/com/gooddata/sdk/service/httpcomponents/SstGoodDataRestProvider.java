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
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * The {@link com.gooddata.sdk.service.GoodDataRestProvider}, which
 * provides configured single endpoint REST connection using standard pre created SST to authenticate.
 */
public final class SstGoodDataRestProvider extends SingleEndpointGoodDataRestProvider {

    /**
     * Create SST REST provider
     * @param endpoint endpoint of GoodData API
     * @param settings settings
     * @param sst super secure token
     */
    public SstGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings, final String sst) {
        super(endpoint, settings, (b, e, s) -> createHttpClient(b, e, sst));
    }

    /**
     * Creates http client using given builder and endpoint, authenticating by sst.
     * @param builder builder to build client from
     * @param endpoint API endpoint to connect client to
     * @param sst token used for authentication
     * @return configured http client
     */
    public static HttpClient createHttpClient(HttpClientBuilder builder, GoodDataEndpoint endpoint, String sst) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");
        notNull(sst, "sst");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new SimpleSSTRetrievalStrategy(sst);
        final HttpHost httpHost = new HttpHost(endpoint.getHostname(), endpoint.getPort(), endpoint.getProtocol());
        return new GoodDataHttpClient(httpClient, httpHost, strategy);
    }
}
