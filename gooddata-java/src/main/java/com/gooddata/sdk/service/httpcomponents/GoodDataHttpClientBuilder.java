/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Custom GoodData http client builder providing custom functionality by descendants of
 * {@link SingleEndpointGoodDataRestProvider}.
 */
@FunctionalInterface
public interface GoodDataHttpClientBuilder {

    /**
     * Builds {@link HttpClient} from given builder, configured to connect to given endpoint applying given settings.
     *
     * @param builder pre-configured builder
     * @param endpoint endpoint of GoodData API
     * @param settings settings
     * @return configured http client.
     */
    HttpClient buildHttpClient(final HttpClientBuilder builder, final GoodDataEndpoint endpoint, final GoodDataSettings settings);
}

