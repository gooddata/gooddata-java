/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to work with GoodData API root.
 */
public class GdcService extends AbstractService {

    public GdcService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Obtains GoodData API root links.
     *
     * @return GoodData API root links
     */
    public Gdc getGdc() {
        try {
            return restTemplate.getForObject(Gdc.URI, Gdc.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get gdc about", e);
        }
    }

}
