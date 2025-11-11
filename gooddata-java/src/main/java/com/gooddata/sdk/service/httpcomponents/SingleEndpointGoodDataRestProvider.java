/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.sdk.common.HttpClient4ComponentsClientHttpRequestFactory;
import com.gooddata.sdk.common.UriPrefixingClientHttpRequestFactory;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.retry.RetryableRestTemplate;
import com.gooddata.sdk.service.util.ResponseErrorHandler;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import static com.gooddata.sdk.common.util.Validate.notNull;
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

    private final Logger logger = LoggerFactory.getLogger(SingleEndpointGoodDataRestProvider.class);

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

    @Override
    public Optional<DataStoreService> getDataStoreService(Supplier<String> stagingUriSupplier) {
        try {
            Class.forName("com.github.sardine.Sardine", false, getClass().getClassLoader());
            return Optional.of(new DataStoreService(this, stagingUriSupplier));
        } catch (ClassNotFoundException e) {
            logger.info("Optional dependency Sardine not found - WebDAV related operations are not supported");
            return Optional.empty();
        }
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
                new HttpClient4ComponentsClientHttpRequestFactory(httpClient),
                URI.create(endpoint.toUri())
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
        final SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(settings.getSocketTimeout()))
                .build();

        final PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(settings.getMaxConnections())
                .setMaxConnTotal(settings.getMaxConnections())
                .setDefaultSocketConfig(socketConfig)
                .build();

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(settings.getConnectionTimeout()))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(settings.getConnectionRequestTimeout()))
                .setResponseTimeout(Timeout.ofMilliseconds(settings.getSocketTimeout()))
                .setCookieSpec(StandardCookieSpec.STRICT)
                .build();

        // CRITICAL: HttpClient 5.x requires explicit cookie store for session management
        // Without this, authentication cookies won't be preserved between requests
        final CookieStore cookieStore = new BasicCookieStore();

        return HttpClientBuilder.create()
                .setUserAgent(settings.getGoodDataUserAgent())
                .setConnectionManager(connectionManager)
                .setDefaultCookieStore(cookieStore)  // Enable cookie/session management
                .addRequestInterceptorFirst(new RequestIdInterceptor())
                .addResponseInterceptorFirst(new ResponseMissingRequestIdInterceptor())
                .setDefaultRequestConfig(requestConfig);
    }
}

