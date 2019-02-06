/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.service.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.gdc.Gdc;
import com.gooddata.sdk.model.gdc.RootLinks;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to work with GoodData API root.
 */
public class GdcService extends AbstractService {

    public GdcService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * @deprecated use GdcService(RestTemplate, GoodDataSettings) constructor instead
     */
    @Deprecated
    public GdcService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Obtains GoodData API root links.
     *
     * @return GoodData API root links
     * @deprecated use {@link #getRootLinks()} instead
     */
    @Deprecated
    public Gdc getGdc() {
        try {
            return restTemplate.getForObject(Gdc.URI, Gdc.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get gdc about", e);
        }
    }

    /**
     * Obtains GoodData API root links.
     *
     * @return GoodData API root links
     */
    public RootLinks getRootLinks() {
        try {
            return restTemplate.getForObject(RootLinks.URI, RootLinks.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get gdc root links", e);
        }
    }

}
