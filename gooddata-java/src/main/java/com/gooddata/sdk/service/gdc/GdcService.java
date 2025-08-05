/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.gdc.RootLinks;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Service to work with GoodData API root.
 */
public class GdcService extends AbstractService {


    // WebClient is injected instead of RestTemplate
    public GdcService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
    }

    /**
     * Obtains GoodData API root links.
     *
     * @return GoodData API root links
     */
    public RootLinks getRootLinks() {
        try {
            // Blocking call for backward compatibility
            return webClient.get()
                    .uri(RootLinks.URI)
                    .retrieve()
                    .bodyToMono(RootLinks.class)
                    .block();
        } catch (WebClientResponseException e) {
            // Exception handling for WebClient
            throw new GoodDataException("Unable to get gdc root links", e);
        }
    }
}
