/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.gdc.DataStoreService;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface for managing REST connection to the GoodData platform using WebClient.
 */
public interface GoodDataRestProvider {

    /**
     * Returns the settings used by this provider.
     */
    GoodDataSettings getSettings();

    /**
     * Returns the configured WebClient instance.
     */
    WebClient getWebClient();

    /**
     * Returns the configured DataStoreService, if provided. By default, returns empty.
     *
     * @param stagingUriSupplier supplier of the data store endpoint
     * @return DataStoreService (empty by default)
     */
    default Optional<DataStoreService> getDataStoreService(final Supplier<String> stagingUriSupplier) {
        return Optional.empty();
    }
}
