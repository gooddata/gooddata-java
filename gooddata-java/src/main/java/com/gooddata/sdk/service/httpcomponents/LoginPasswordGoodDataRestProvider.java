/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * The default {@link com.gooddata.sdk.service.GoodDataRestProvider} used internally by {@link com.gooddata.sdk.service.GoodData}.
 * Provides configured single endpoint REST connection using standard GoodData login and password authentication.
 *
 * See https://help.gooddata.com/display/API/API+Reference#/reference/authentication/log-in
 */
public final class LoginPasswordGoodDataRestProvider extends SingleEndpointGoodDataRestProvider {

    /**
     * Creates new instance.
     * @param endpoint endpoint of GoodData API
     * @param settings settings
     * @param login API user login
     * @param password API user password
     */
    public LoginPasswordGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings,
                                             final String login, final String password) {
        super(endpoint, settings, (builder, builderEndpoint, builderSettings) -> createHttpClient(builder, builderEndpoint, login, password));
    }

    /**
     * Creates http client using given builder and endpoint, authenticating by login and password.
     * @param builder builder to build client from
     * @param endpoint API endpoint to connect client to
     * @param login login
     * @param password password
     * @return configured http client
     */
    public static HttpClient createHttpClient(final HttpClientBuilder builder, final GoodDataEndpoint endpoint,
                                              final String login, final String password) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");
        notNull(login, "login");
        notNull(password, "password");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(login, password);
        final HttpHost httpHost = new HttpHost(endpoint.getHostname(), endpoint.getPort(), endpoint.getProtocol());
        return new GoodDataHttpClient(httpClient, httpHost, strategy);
    }
}
