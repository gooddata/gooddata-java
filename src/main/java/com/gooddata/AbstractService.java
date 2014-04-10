/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 */
public abstract class AbstractService {
    protected final RestTemplate restTemplate;

    public AbstractService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T poll(URI pollingUri, ConditionCallback<T> condition /*optional*/, Class<T> cls) {

        while (true) {

            final ResponseEntity<T> entity = restTemplate.getForEntity(pollingUri, cls);

            if (condition.finished(entity)) {
                return entity.getBody();
            }

            //TODO maxcount, retries, handle errors
        }
    }

    public static interface ConditionCallback<T> {
        boolean finished(HttpEntity<T> entity);
    }

}
