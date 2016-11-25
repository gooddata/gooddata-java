/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.authentication;

import com.gooddata.GoodDataEndpoint;
import com.gooddata.Authentication;
import com.gooddata.http.client.GoodDataHttpClient;
import com.gooddata.http.client.LoginSSTRetrievalStrategy;
import com.gooddata.http.client.SSTRetrievalStrategy;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Login and password authentication
 */
public class LoginPasswordAuthentication implements Authentication {

    private final String login;
    private final String password;

    /**
     * Create login password authentication
     *
     * @param login    login
     * @param password password
     */
    public LoginPasswordAuthentication(final String login, final String password) {
        this.login = notEmpty(login, "login");
        this.password = notEmpty(password, "password");
    }

    @Override
    public HttpClient createHttpClient(final GoodDataEndpoint endpoint, final HttpClientBuilder builder) {
        notNull(endpoint, "endpoint");
        notNull(builder, "builder");

        final HttpClient httpClient = builder.build();
        final SSTRetrievalStrategy strategy = new LoginSSTRetrievalStrategy(login, password);
        final HttpHost httpHost = new HttpHost(endpoint.getHostname(), endpoint.getPort(), endpoint.getProtocol());
        return new GoodDataHttpClient(httpClient, httpHost, strategy);
    }

}
