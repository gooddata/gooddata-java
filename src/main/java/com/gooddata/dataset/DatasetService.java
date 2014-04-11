/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractService;
import org.springframework.web.client.RestTemplate;

/**
 */
public class DatasetService extends AbstractService {

    public DatasetService(RestTemplate restTemplate) {
        super(restTemplate);
    }

}
