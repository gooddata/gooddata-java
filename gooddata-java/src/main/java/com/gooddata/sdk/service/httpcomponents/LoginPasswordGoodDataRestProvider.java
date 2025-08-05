/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.sdk.service.GoodDataEndpoint;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * GoodDataRestProvider using login/password, upgraded to use reactive WebClient.
 */
public final class LoginPasswordGoodDataRestProvider extends SingleEndpointGoodDataRestProvider {

    public LoginPasswordGoodDataRestProvider(
            final GoodDataEndpoint endpoint,
            final GoodDataSettings settings,
            final String login,
            final String password) {
        super(endpoint, settings, createWebClientWithBasicAuth(endpoint, settings, login, password));
    }

    public LoginPasswordGoodDataRestProvider(
            final GoodDataEndpoint endpoint,
            final GoodDataSettings settings,
            final String login,
            final String password,
            final WebClient webClient) {
        super(endpoint, settings, webClient);
    }

    private static WebClient createWebClientWithBasicAuth(
            GoodDataEndpoint endpoint,
            GoodDataSettings settings,
            String login,
            String password) {
        String auth = login + ":" + password;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        return WebClient.builder()
                .baseUrl(endpoint.toUri().toString())
                .defaultHeaders(headers -> {
                    settings.getPresetHeaders().forEach(headers::add);
                    headers.add("Authorization", authHeader);
                })
                .defaultHeader("User-Agent", settings.getGoodDataUserAgent())
                .build();
    }
}