/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.DataStoreException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.io.InputStream;

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
        try {
            return restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, project.getId(), datasetId);
        } catch (GoodDataRestException e) {
            if (e.getStatusCode() == 404) {
                throw new DatasetNotFoundException(datasetId, e);
            } else {
                throw new DatasetException("Unable to get dataset " + datasetId, e);
            }
        } catch (RestClientException e) {
            throw new DatasetException("Unable to get dataset " + datasetId, e);
        }
    }

    public void loadDataset(Project project, InputStream dataset, DatasetManifest manifest) {
        final String dirPath = getDirPath(project);
        try {
            dataStoreService.upload(dirPath + manifest.getFile(), dataset);
            final String manifestJson = mapper.writeValueAsString(manifest);
            dataStoreService.upload(dirPath + MANIFEST_FILE_NAME, IOUtils.toInputStream(manifestJson));

            final PullTask pullTask = restTemplate.postForObject(Pull.URI, new Pull(dirPath), PullTask.class, project.getId());
            final PullTaskStatus taskStatus = poll(pullTask.getUri(), new ConditionCallback() {
                @Override
                public boolean finished(ClientHttpResponse response) throws IOException {
                    final PullTaskStatus status = extractData(response, PullTaskStatus.class);
                    return status.isFinished();
                }
            }, PullTaskStatus.class);
            if (!taskStatus.isSuccess()) {
                throw new DatasetException("ETL pull finished with status " + taskStatus.getStatus());
            }
        } catch (IOException e) {
            throw new DatasetException("Unable to serialize manifest", e);
        } catch (DataStoreException | GoodDataRestException | RestClientException e) {
            throw new DatasetException("Unable to load dataset " + manifest.getDataSet(), e);
        } finally {
            try {
                dataStoreService.delete(dirPath);
            } catch (DataStoreException ignored) {
                // todo log?
            }
        }

    }

    private String getDirPath(Project project) {
        return new StringBuilder("/")
                .append(project.getId())
                .append("_")
                .append(RandomStringUtils.randomAlphabetic(3))
                .append("/")
                .toString();
    }

    public void loadDataset(Project project, String datasetId, InputStream dataset) {
        loadDataset(project, dataset, getDatasetManifest(project, datasetId));
    }
}
