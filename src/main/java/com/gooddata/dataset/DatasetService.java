/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.DataStoreException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;

/**
 * Service to work with datasets and manifests.
 */
public class DatasetService extends AbstractService {

    private static final String MANIFEST_FILE_NAME = "upload_info.json";
    private static final String STATUS_FILE_NAME = "upload_status.json";

    private final DataStoreService dataStoreService;

    public DatasetService(RestTemplate restTemplate, DataStoreService dataStoreService) {
        super(restTemplate);
        this.dataStoreService = notNull(dataStoreService, "dataStoreService");
    }

    /**
     * Obtains manifest from given project by given datasetId
     *
     * @param project   project to which manifest belongs
     * @param datasetId id of dataset
     * @return manifest for dataset
     * @throws com.gooddata.dataset.DatasetNotFoundException when manifest can't be found (doesn't exist)
     * @throws com.gooddata.dataset.DatasetException         in case the API call failure
     */
    public DatasetManifest getDatasetManifest(Project project, String datasetId) {
        notNull(project, "project");
        notEmpty(datasetId, "datasetId");
        try {
            return restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, project.getId(), datasetId);
        } catch (GoodDataRestException e) {
            if (e.getStatusCode() == 404) {
                throw new DatasetNotFoundException(datasetId, e);
            } else {
                throw new DatasetException("Unable to get manifest", datasetId, e);
            }
        } catch (RestClientException e) {
            throw new DatasetException("Unable to get manifest", datasetId, e);
        }
    }

    /**
     * Loads dataset into platform. Uploads given dataset and manifest to staging area and triggers ETL pull.
     * The call is asynchronous returning {@link com.gooddata.FutureResult} to let caller wait for results.
     * Uploaded files are deleted from staging area when finished.
     *
     * @param project  project to which dataset belongs
     * @param manifest dataset manifest
     * @param dataset  dataset to upload
     * @return {@link com.gooddata.FutureResult} of the task, which can throw {@link com.gooddata.dataset.DatasetException}
     * in case the ETL pull task fails
     * @throws com.gooddata.dataset.DatasetException if there is a problem to serialize manifest or upload dataset
     */
    public FutureResult<Void> loadDataset(final Project project, final DatasetManifest manifest,
                                          final InputStream dataset) {
        notNull(project, "project");
        notNull(dataset, "dataset");
        notNull(manifest, "manifest");
        final Path dirPath = Paths.get("/", project.getId() + "_" + RandomStringUtils.randomAlphabetic(3), "/");
        try {
            dataStoreService.upload(dirPath.resolve(manifest.getFile()).toString(), dataset);
            final String manifestJson = mapper.writeValueAsString(manifest);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(manifestJson.getBytes(UTF_8));
            dataStoreService.upload(dirPath.resolve(MANIFEST_FILE_NAME).toString(), inputStream);

            final PullTask pullTask = restTemplate
                    .postForObject(Pull.URI, new Pull(dirPath.toString()), PullTask.class, project.getId());
            return new FutureResult<>(this, new SimplePollHandler<Void>(pullTask.getUri(), Void.class) {
                @Override
                public boolean isFinished(ClientHttpResponse response) throws IOException {
                    final PullTaskStatus status = extractData(response, PullTaskStatus.class);
                    final boolean finished = status.isFinished();
                    if (finished && !status.isSuccess()) {
                        String message = "status: " + status.getStatus();
                        try {
                            final InputStream input = dataStoreService
                                    .download(dirPath.resolve(STATUS_FILE_NAME).toString());
                            final FailStatus failStatus = mapper.readValue(input, FailStatus.class);
                            if (failStatus != null && failStatus.getError() != null) {
                                message = failStatus.getError().getFormattedMessage();
                            }
                        } catch (IOException | DataStoreException ignored) {
                            // todo log?
                        }
                        throw new DatasetException(message, manifest.getDataSet());
                    }
                    return finished;
                }

                @Override
                protected void onFinish() {
                    try {
                        dataStoreService.delete(dirPath.toString() + "/");
                    } catch (DataStoreException ignored) {
                        // todo log?
                    }
                }
            });
        } catch (IOException e) {
            throw new DatasetException("Unable to serialize manifest", manifest.getDataSet(), e);
        } catch (DataStoreException | GoodDataRestException | RestClientException e) {
            throw new DatasetException("Unable to load", manifest.getDataSet(), e);
        }
    }

    /**
     * Gets DatasetManifest (using {@link #getDatasetManifest(com.gooddata.project.Project, String)}
     * first and then calls {@link #loadDataset(com.gooddata.project.Project, DatasetManifest, java.io.InputStream)}
     *
     * @param project   project to which dataset belongs
     * @param datasetId datasetId to obtain a manifest
     * @param dataset   dataset to upload
     * @return {@link com.gooddata.FutureResult} of the task
     */
    public FutureResult<Void> loadDataset(Project project, String datasetId, InputStream dataset) {
        notNull(project, "project");
        notEmpty(datasetId, "datasetId");
        notNull(dataset, "dataset");
        return loadDataset(project, getDatasetManifest(project, datasetId), dataset);
    }

    /**
     * Lists datasets in project. Returns empty list in case there are no datasets.
     *
     * @param project project to list datasets in
     * @return collection of datasets in project or empty list
     */
    public Collection<Dataset> listDatasets(Project project) {
        notNull(project, "project");
        try {
            final Datasets result = restTemplate.getForObject(Datasets.URI, Datasets.class, project.getId());
            if (result == null || result.getLinks() == null) {
                return emptyList();
            }
            return result.getLinks();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list datasets for project " + project.getId(), e);
        }
    }
}
