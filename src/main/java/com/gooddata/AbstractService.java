/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 */
public abstract class AbstractService {

    public static Integer WAIT_BEFORE_RETRY_IN_MILLIS = 5 * 1000;
    public static Integer MAX_ATTEMPTS = 5;

    protected final RestTemplate restTemplate;

    public AbstractService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T poll(URI pollingUri, Class<T> cls) {
        return poll(pollingUri, new ConditionCallback<T>() {
            @Override
            public boolean finished(ResponseEntity<T> entity) {
                return HttpStatus.CREATED.equals(entity.getStatusCode());
            }
        }, cls);
    }

    public <T> T poll(URI pollingUri, ConditionCallback<T> condition /*optional*/, Class<T> cls) {
        int attempt = 0;

        while (true) {

            final ResponseEntity<T> entity = restTemplate.getForEntity(pollingUri, cls);

            if (condition.finished(entity)) {
                return entity.getBody();
            }
            //TODO ? handle errors

            if (attempt >= MAX_ATTEMPTS - 1) {
                //TODO ? log, return value
                return null;
            }

            try {
                Thread.sleep(WAIT_BEFORE_RETRY_IN_MILLIS);
            } catch (InterruptedException e) {
                // do nothing
            }
            attempt++;
        }
    }

    public static interface ConditionCallback<T> {
        boolean finished(ResponseEntity<T> entity);
    }

}
