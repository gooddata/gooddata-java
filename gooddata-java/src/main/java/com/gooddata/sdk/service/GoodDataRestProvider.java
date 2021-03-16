/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.gdc.DataStoreService;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The main interface responsible for GoodData platform REST connection management.
 * Should provide completely configured {@link RestTemplate} capable to perform valid
 * communication with GoodData platform REST API. Mainly the following functionality should
 * be provided:
 * <ul>
 *     <li>prefixing the URI by API endpoint - services use only path part of URI</li>
 *     <li>authentication</li>
 *     <li>applying {@link GoodDataSettings} - especially user agent, headers and connection settings</li>
 *     <li>configuring proper error handler (i.e. {@link com.gooddata.sdk.service.util.ResponseErrorHandler})</li>
 * </ul>
 *
 * The default implementation (internally used by {@link GoodData} is {@link com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider}.
 */
public interface GoodDataRestProvider {

    /**
     * Settings used by the provider.
     *
     * @return used settings
     */
    GoodDataSettings getSettings();

    /**
     * Configured RestTemplate instance.
     *
     * @return provided RestTemplate
     */
    RestTemplate getRestTemplate();

    /**
     * Configured DataStoreService if provided. By default empty.
     *
     * @param stagingUriSupplier supplier of the data store endpoint
     * @return dataStoreService (empty by default)
     */
    default Optional<DataStoreService> getDataStoreService(final Supplier<String> stagingUriSupplier) {
        return Optional.empty();
    }
}
