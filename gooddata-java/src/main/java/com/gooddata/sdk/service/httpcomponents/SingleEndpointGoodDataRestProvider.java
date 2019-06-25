/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.UriPrefixingClientHttpRequestFactory;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.retry.RetryableRestTemplate;
import com.gooddata.sdk.service.util.ResponseErrorHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * {@link GoodDataRestProvider} capable to be used with single API endpoint using the
 * Apache {@link  HttpClient} to perform HTTP operations. It provides following functionality:
 * <ul>
 *     <li>Prepends the URI path with API endpoint (using {@link UriPrefixingClientHttpRequestFactory}</li>
 *     <li>Configures {@link ResponseErrorHandler}</li>
 *     <li>Configures connection according to {@link GoodDataSettings}</li>
 *     <li>Set default headers from {@link GoodDataSettings} including User-Agent</li>
 *     <li>Configures retries in case it's requested</li>
 * </ul>
 *
 * To provide complete implementation, this class must be extended and descendants should implement own logic by providing
 * {@link GoodDataHttpClientBuilder} to the constructor. Namely the authentication logic remains to be provided by descendants.
 */
public abstract class SingleEndpointGoodDataRestProvider implements GoodDataRestProvider {

    protected final GoodDataEndpoint endpoint;
    protected final GoodDataSettings settings;

    protected HttpClient httpClient;
    protected RestTemplate restTemplate;

    /**
     * Creates new instance.
     *
     * @param endpoint API endpoint
     * @param settings settings
     * @param builder custom GoodData http client builder
     */
    protected SingleEndpointGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings,
                                                 final GoodDataHttpClientBuilder builder) {
        this.endpoint = endpoint;
        this.settings = settings;
        this.restTemplate = createRestTemplate(endpoint, settings, builder.buildHttpClient(
                createHttpClientBuilder(settings), endpoint, settings));
    }

    @Override
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Override
    public GoodDataSettings getSettings() {
        return settings;
    }

    /**
     * @return used API endpoint
     */
    public GoodDataEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @return configured http client
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Creates configured REST template
     * @param endpoint API endpoint
     * @param settings settings
     * @param httpClient http client to build RestTemplate on
     * @return configured REST template
     */
    protected RestTemplate createRestTemplate(final GoodDataEndpoint endpoint, final GoodDataSettings settings, final HttpClient httpClient) {
        notNull(endpoint, "endpoint");
        notNull(settings, "settings");
        this.httpClient = notNull(httpClient, "httpClient");

        final UriPrefixingClientHttpRequestFactory factory = new UriPrefixingClientHttpRequestFactory(
                new HttpComponentsClientHttpRequestFactory(httpClient),
                endpoint.toUri()
        );

        final RestTemplate restTemplate;
        if (settings.getRetrySettings() == null) {
            restTemplate = new RestTemplate(factory);
        } else {
            restTemplate = RetryableRestTemplate.create(settings.getRetrySettings(), factory);
        }
        restTemplate.setInterceptors(asList(
                new HeaderSettingRequestInterceptor(settings.getPresetHeaders()),
                new DeprecationWarningRequestInterceptor()));

        restTemplate.setErrorHandler(new ResponseErrorHandler(restTemplate.getMessageConverters()));

        return restTemplate;
    }

    /**
     * Creates http client builder, applying given settings.
     * @param settings settings to apply
     * @return configured builder
     */
    protected HttpClientBuilder createHttpClientBuilder(final GoodDataSettings settings) {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(settings.getMaxConnections());
        connectionManager.setMaxTotal(settings.getMaxConnections());

        final SocketConfig.Builder socketConfig = SocketConfig.copy(SocketConfig.DEFAULT);
        socketConfig.setSoTimeout(settings.getSocketTimeout());
        connectionManager.setDefaultSocketConfig(socketConfig.build());

        final RequestConfig.Builder requestConfig = RequestConfig.copy(RequestConfig.DEFAULT);
        requestConfig.setConnectTimeout(settings.getConnectionTimeout());
        requestConfig.setConnectionRequestTimeout(settings.getConnectionRequestTimeout());
        requestConfig.setSocketTimeout(settings.getSocketTimeout());

        return HttpClientBuilder.create()
                .setUserAgent(settings.getGoodDataUserAgent())
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig.build());
    }
}
