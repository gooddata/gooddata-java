/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import com.gooddata.task.PullTask;
import com.gooddata.task.PullTaskStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 */
public class DatasetService extends AbstractService {

    private static final String MANIFEST_FILE_NAME = "upload_info.json";
    private final DataStoreService dataStoreService;
    private final static ObjectMapper mapper = new ObjectMapper();

    public DatasetService(RestTemplate restTemplate, DataStoreService dataStoreService) {
        super(restTemplate);
        this.dataStoreService = dataStoreService;
    }

    public DatasetManifest getDatasetManifest(Project project, String datasetId) {
        return restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, project.getId(), datasetId);
    }

    public void loadDataset(Project project, InputStream dataset, DatasetManifest manifest) {
        final String dirPath = "/" + project.getId() + "_" + RandomStringUtils.randomAlphabetic(3) + "/";
        try {
            dataStoreService.upload(dirPath + manifest.getFile(), dataset);
            final String manifestJson = mapper.writeValueAsString(manifest);
            dataStoreService.upload(dirPath + MANIFEST_FILE_NAME, IOUtils.toInputStream(manifestJson));

            final PullTask pullTask = restTemplate.postForObject(Pull.URI, new Pull(dirPath), PullTask.class, project.getId());
            PullTaskStatus taskStatus = poll(URI.create(pullTask.getUri()), new ConditionCallback() {
                @Override
                public boolean finished(ClientHttpResponse response) throws IOException {
                    final PullTaskStatus status = new HttpMessageConverterExtractor<>(PullTaskStatus.class, restTemplate.getMessageConverters())
                            .extractData(response);
                    return status.isFinished();
                }
            }, PullTaskStatus.class);
            if (!taskStatus.isSuccess()) {
                throw new GoodDataException("ETL pull finished with status " + taskStatus.getStatus());
            }
        } catch (IOException e) {
            throw new GoodDataException("Unable to serialize manifest", e);
        } finally {
            dataStoreService.delete(dirPath);
        }

    }

    public void loadDataset(Project project, InputStream dataset, String datasetId) {
        loadDataset(project, dataset, getDatasetManifest(project, datasetId));
    }
}
