/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.DataStoreException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.gdc.TaskStatus;
import com.gooddata.gdc.UriResponse;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isEmpty;

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

            return pullLoad(project, dirPath, manifest.getDataSet());
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

    public FutureResult<Void> loadDatasets(final Project project, DatasetManifest... datasets) {
        return loadDatasets(project, asList(datasets));
    }

    /**
     * Loads datasets into platform. Uploads given datasets and their manifests to staging area and triggers ETL pull.
     * The call is asynchronous returning {@link com.gooddata.FutureResult} to let caller wait for results.
     * Uploaded files are deleted from staging area when finished.
     *
     * @param project  project to which dataset belongs
     * @param datasets map dataset manifests
     * @return {@link com.gooddata.FutureResult} of the task, which can throw {@link com.gooddata.dataset.DatasetException}
     * in case the ETL pull task fails
     * @throws com.gooddata.dataset.DatasetException if there is a problem to serialize manifest or upload dataset
     * @see <a href="https://developer.gooddata.com/article/multiload-of-csv-data">batch upload reference</a>
     */
    public FutureResult<Void> loadDatasets(final Project project, final Collection<DatasetManifest> datasets) {
        notNull(project, "project");
        validateUploadManifests(datasets);
        final List<String> datasetsNames = new ArrayList<>(datasets.size());
        try {
            final Path dirPath = Paths.get("/", project.getId() + "_" + RandomStringUtils.randomAlphabetic(3), "/");
            for (DatasetManifest datasetManifest : datasets) {
                datasetsNames.add(datasetManifest.getDataSet());
                dataStoreService.upload(dirPath.resolve(datasetManifest.getFile()).toString(), datasetManifest.getSource());
            }

            final String manifestJson = mapper.writeValueAsString(new DatasetManifests(datasets));
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(manifestJson.getBytes(UTF_8));
            dataStoreService.upload(dirPath.resolve(MANIFEST_FILE_NAME).toString(), inputStream);

            return pullLoad(project, dirPath, datasetsNames);
        } catch (IOException e) {
            throw new DatasetException("Unable to serialize manifest", datasetsNames, e);
        } catch (DataStoreException | GoodDataRestException | RestClientException e) {
            throw new DatasetException("Unable to load", datasetsNames, e);
        }
    }

    private void validateUploadManifests(final Collection<DatasetManifest> datasets) {
        notEmpty(datasets, "datasets");
        for (DatasetManifest datasetManifest : datasets) {
            if (datasetManifest.getSource() == null) {
                throw new IllegalArgumentException(format("Source for dataset '%s' is null", datasetManifest.getDataSet()));
            }
            if (datasetManifest.getFile() == null) {
                throw new IllegalArgumentException(format("File for dataset '%s' is null", datasetManifest.getDataSet()));
            }
            if (isEmpty(datasetManifest.getDataSet())) {
                throw new IllegalArgumentException("Dataset name is empty.");
            }
        }
    }

    private FutureResult<Void> pullLoad(Project project, final Path dirPath, final String dataset) {
        return pullLoad(project, dirPath, asList(dataset));
    }

    private FutureResult<Void> pullLoad(Project project, final Path dirPath, final Collection<String> datasets) {
        final PullTask pullTask = restTemplate
                .postForObject(Pull.URI, new Pull(dirPath.toString()), PullTask.class, project.getId());
        return new FutureResult<>(this, new SimplePollHandler<Void>(pullTask.getUri(), Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                final PullTaskStatus status = extractData(response, PullTaskStatus.class);
                final boolean finished = status.isFinished();
                if (finished && !status.isSuccess()) {
                    final String message = getErrorMessage(status, dirPath);
                    throw new DatasetException(message, datasets);
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

    }

    private String getErrorMessage(final PullTaskStatus status, final Path dirPath) {
        String message = "status: " + status.getStatus();
        try {
            final FailStatus failStatus = download(dirPath.resolve(STATUS_FILE_NAME), FailStatus.class);
            if (failStatus != null) {
                final List<FailPart> errorParts = failStatus.getErrorParts();
                if (!errorParts.isEmpty()) {
                    final List<String> errors = new ArrayList<>();
                    for (FailPart part: errorParts) {
                        if (part.getLogName() != null) {
                            try {
                                final String[] msg = download(dirPath.resolve(part.getLogName()), String[].class);
                                errors.addAll(asList(msg));
                            } catch (IOException | DataStoreException e) {
                                if (part.getError() != null) {
                                    errors.add(part.getError().getFormattedMessage());
                                }
                            }
                        }
                    }
                    message = errors.toString();
                } else if (failStatus.getError() != null) {
                    message = failStatus.getError().getFormattedMessage();
                }
            }
        } catch (IOException | DataStoreException ignored) {
            // todo log?
        }
        return message;
    }

    private <T> T download(final Path path, final Class<T> type) throws IOException {
        try (final InputStream input = dataStoreService.download(path.toString())) {
            return mapper.readValue(input, type);
        }
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

    /**
     * Optimize SLI hash. This feature is useful only if data warehouse was reduced somehow. Remove unused values from
     * the existing SLI hash.
     *
     * @param project project to optimize SLI hash in
     * @return {@link com.gooddata.FutureResult} of the task
     */
    public FutureResult<Void> optimizeSliHash(final Project project) {
        notNull(project, "project");

        final UriResponse uriResponse = restTemplate.postForObject(
                EtlMode.URL, new EtlMode(EtlModeType.SLI, LookupMode.RECREATE), UriResponse.class, project.getId());

        return new FutureResult<>(this,
                new AbstractPollHandler<TaskStatus, Void>(uriResponse.getUri(), TaskStatus.class, Void.class) {
            @Override
            public void handlePollResult(final TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new GoodDataException("Unable to optimize SLI hash for project " + project.getId());
                }
                setResult(null);
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                if (!super.isFinished(response)) {
                    return false;
                }
                final TaskStatus maqlDdlTaskStatus = extractData(response, TaskStatus.class);
                if (maqlDdlTaskStatus.isSuccess()) {
                    return true;
                }
                throw new GoodDataException("Unable to optimize SLI hash: " + maqlDdlTaskStatus.getMessages());
            }

        });

    }

    /**
     * Update project data with the given update script (MAQL). This method can be used for data manipulation only,
     * for model changes use {@link com.gooddata.model.ModelService#updateProjectModel}.
     *
     * @param project project to be updated
     * @param maqlDml update script to be executed in the project
     * @return poll result
     *
     * @see com.gooddata.model.ModelService#updateProjectModel
     */
    public FutureResult<Void> updateProjectData(final Project project, final String maqlDml) {
        notNull(project, "project");

        final UriResponse uriResponse = restTemplate.postForObject(
                MaqlDml.URI, new MaqlDml(maqlDml), UriResponse.class, project.getId());

        final String errorMessage = format("Unable to update data for project '%s'", project.getId());

        return new FutureResult<>(this,
                new AbstractPollHandler<TaskState, Void>(uriResponse.getUri(), TaskState.class, Void.class) {
            @Override
            public void handlePollResult(final TaskState pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new GoodDataException(errorMessage);
                }
                setResult(null);
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                if (!super.isFinished(response)) {
                    return false;
                }
                final TaskState taskState = extractData(response, TaskState.class);
                if (taskState.isSuccess()) {
                    return true;
                }
                throw new GoodDataException(errorMessage + ": " + taskState.getMessage());
            }
        });
    }

}
