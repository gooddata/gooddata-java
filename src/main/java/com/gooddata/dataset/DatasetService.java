/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.springframework.web.client.RestTemplate;

/**
 */
public class DatasetService extends AbstractService {

    private final DataStoreService dataStoreService;

    public DatasetService(RestTemplate restTemplate, DataStoreService dataStoreService) {
        super(restTemplate);
        this.dataStoreService = dataStoreService;
    }

    public DatasetManifest getDatasetManifest(Project project, String datasetId) {
        return restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, project.getId(), datasetId);
    }

}
