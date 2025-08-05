/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.sdk.common.UriPrefixingWebClient;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.gdc.DataStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.function.Supplier;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Abstract base provider for GoodData services using a single API endpoint and reactive WebClient.
 */
public abstract class SingleEndpointGoodDataRestProvider implements GoodDataRestProvider {

    private final Logger logger = LoggerFactory.getLogger(SingleEndpointGoodDataRestProvider.class);

    protected final GoodDataEndpoint endpoint;
    protected final GoodDataSettings settings;
    protected final WebClient webClient;

    /**
     * Creates a new provider instance.
     *
     * @param endpoint API endpoint
     * @param settings configuration settings
     */
    protected SingleEndpointGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings) {
        this.endpoint = notNull(endpoint, "endpoint");
        this.settings = notNull(settings, "settings");

        // You can use UriPrefixingWebClient here if needed, or just use WebClient.builder().baseUrl(endpoint.getUrl()).build();
        this.webClient = createWebClient(endpoint, settings);
    }

    protected SingleEndpointGoodDataRestProvider(final GoodDataEndpoint endpoint, final GoodDataSettings settings, WebClient webClient) {
        this.endpoint = notNull(endpoint, "endpoint");
        this.settings = notNull(settings, "settings");
        this.webClient = notNull(webClient, "webClient");
    }


    /**
     * Returns the configured reactive WebClient.
     */
    public WebClient getWebClient() {
        return webClient;
    }

    @Override
    public GoodDataSettings getSettings() {
        return settings;
    }

    @Override
    public Optional<DataStoreService> getDataStoreService(Supplier<String> stagingUriSupplier) {
        try {
            Class.forName("com.github.sardine.Sardine", false, getClass().getClassLoader());
            // pass the WebClient instance
            return Optional.of(new DataStoreService(this, stagingUriSupplier, webClient));
        } catch (ClassNotFoundException e) {
            logger.info("Optional dependency Sardine not found - WebDAV related operations are not supported");
            return Optional.empty();
        }
    }


    public GoodDataEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Creates a WebClient instance with custom settings (e.g. headers, User-Agent, endpoint base URL).
     *
     * @param endpoint API endpoint
     * @param settings configuration settings
     * @return configured WebClient instance
     */
    protected WebClient createWebClient(final GoodDataEndpoint endpoint, final GoodDataSettings settings) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(endpoint.toUri().toString())
                .defaultHeaders(headers -> settings.getPresetHeaders().forEach(headers::add))
                .defaultHeader("User-Agent", settings.getGoodDataUserAgent());

        // If you need proxy or timeouts, add .clientConnector(...) here.
        // If you want to use UriPrefixingWebClient, use it as a wrapper:
        // return new UriPrefixingWebClient(builder, endpoint.toUri()).getWebClient();

        return builder.build();
    }
}
