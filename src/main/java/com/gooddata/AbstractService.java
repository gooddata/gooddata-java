/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.web.client.RestTemplate;

/**
 */
public abstract class AbstractService {
    protected final RestTemplate restTemplate;

    public AbstractService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
