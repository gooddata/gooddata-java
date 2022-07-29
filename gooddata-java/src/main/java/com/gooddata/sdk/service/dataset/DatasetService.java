/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataset;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.dataset.*;
import com.gooddata.sdk.model.gdc.AboutLinks.Link;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.gdc.DataStoreException;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.project.model.ModelService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Service to work with datasets, manifests and dataset uploads.
 */
public class DatasetService extends AbstractService {

    public static final UriTemplate UPLOADS_INFO_TEMPLATE = new UriTemplate(UploadsInfo.URI);
    private static final String MANIFEST_FILE_NAME = "upload_info.json";
    private static final String ETL_PULL_DEFAULT_ERROR_MESSAGE = "ETL Pull failed with status %s";

    private final DataStoreService dataStoreService;

    public DatasetService(final RestTemplate restTemplate, final DataStoreService dataStoreService,
                          final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.dataStoreService = dataStoreService;
    }

    /**
     * Obtains manifest from given project by given datasetId
     *
     * @param project   project to which manifest belongs
     * @param datasetId id of dataset
     * @return manifest for dataset
     * @throws DatasetNotFoundException when manifest can't be found (doesn't exist)
     * @throws DatasetException         in case the API call failure
     */
    public DatasetManifest getDatasetManifest(Project project, String datasetId) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
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
     * The call is asynchronous returning {@link FutureResult} to let caller wait for results.
     * Uploaded files are deleted from staging area when finished.
     *
     * @param project  project to which dataset belongs
     * @param manifest dataset manifest
     * @param dataset  dataset to upload
     * @return {@link FutureResult} of the task, which can throw {@link DatasetException}
     * in case the ETL pull task fails
     * @throws DatasetException if there is a problem to serialize manifest or upload dataset
     */
    public FutureResult<Void> loadDataset(final Project project, final DatasetManifest manifest,
                                          final InputStream dataset) {
        notNull(project, "project");
        notNull(dataset, "dataset");
        notNull(manifest, "manifest");
        manifest.setSource(dataset);

        return loadDatasets(project, manifest);
    }

    /**
     * Gets DatasetManifest (using {@link #getDatasetManifest(Project, String)}
     * first and then calls {@link #loadDataset(Project, DatasetManifest, java.io.InputStream)}
     *
     * @param project   project to which dataset belongs
     * @param datasetId datasetId to obtain a manifest
     * @param dataset   dataset to upload
     * @return {@link FutureResult} of the task
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
     * The call is asynchronous returning {@link FutureResult} to let caller wait for results.
     * Uploaded files are deleted from staging area when finished.
     *
     * @param project  project to which dataset belongs
     * @param datasets map dataset manifests
     * @return {@link FutureResult} of the task, which can throw {@link DatasetException}
     * in case the ETL pull task fails
     * @throws DatasetException if there is a problem to serialize manifest or upload dataset
     * @see <a href="https://developer.gooddata.com/article/multiload-of-csv-data">batch upload reference</a>
     */
    public FutureResult<Void> loadDatasets(final Project project, final Collection<DatasetManifest> datasets) {
        if (dataStoreService == null) {
            throw new UnsupportedOperationException("WebDAV calls not supported. Please add com.github.lookfirst:sardine to dependencies.");
        }

        notNull(project, "project");
        validateUploadManifests(datasets);
        final List<String> datasetsNames = new ArrayList<>(datasets.size());
        try {
            final String dirPath = "/" + project.getId() + "_" + RandomStringUtils.randomAlphabetic(3) + "/";
            for (DatasetManifest datasetManifest : datasets) {
                datasetsNames.add(datasetManifest.getDataSet());
                dataStoreService.upload(dirPath + datasetManifest.getFile(), datasetManifest.getSource());
            }

            final String manifestJson = mapper.writeValueAsString(new DatasetManifests(datasets));
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(manifestJson.getBytes(UTF_8));
            dataStoreService.upload(dirPath + MANIFEST_FILE_NAME, inputStream);

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

    private FutureResult<Void> pullLoad(Project project, final String dirPath, final Collection<String> datasets) {
        notNull(project.getId(), "project.id");
        final PullTask pullTask = restTemplate
                .postForObject(Pull.URI, new Pull(dirPath), PullTask.class, project.getId());

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, Void>(
                notNullState(pullTask, "created pull task").getPollUri(), TaskStatus.class, Void.class) {
            @Override
            public void handlePollResult(TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    final String message = isEmpty(pollResult.getMessages())
                            ? String.format(ETL_PULL_DEFAULT_ERROR_MESSAGE, pollResult.getStatus())
                            : pollResult.getMessages().toString();
                    throw new DatasetException(message, datasets);
                }
                setResult(null);
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new DatasetException("Unable to load", datasets, e);
            }

            @Override
            protected void onFinish() {
                try {
                    dataStoreService.delete(dirPath);
                } catch (DataStoreException ignored) {
                    // todo log?
                }
            }
        });
    }

    /**
     * Lists datasets (links) in project. Returns empty list in case there are no datasets.
     *
     * @param project project to list datasets in
     * @return collection of dataset links or empty list
     */
    public Collection<Link> listDatasetLinks(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        try {
            final DatasetLinks result = restTemplate.getForObject(DatasetLinks.URI, DatasetLinks.class, project.getId());
            if (result == null) {
                throw new GoodDataException("Empty response from API call");
            } else if (result.getLinks() == null) {
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
     * @return {@link FutureResult} of the task
     */
    public FutureResult<Void> optimizeSliHash(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        final UriResponse uriResponse = restTemplate.postForObject(
                EtlMode.URL, new EtlMode(EtlModeType.SLI, LookupMode.RECREATE), UriResponse.class, project.getId());

        return new PollResult<>(this,
                new AbstractPollHandler<TaskStatus, Void>(
                        notNullState(uriResponse, "created optimize task").getUri(),
                        TaskStatus.class, Void.class) {
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

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException("Unable to optimize SLI hash: " + getPollingUri(), e);
            }

        });

    }

    /**
     * Update project data with the given update script (MAQL). This method can be used for data manipulation only,
     * for model changes use {@link ModelService#updateProjectModel}.
     *
     * @param project project to be updated
     * @param maqlDml update script to be executed in the project
     * @return poll result
     * @see ModelService#updateProjectModel
     */
    public FutureResult<Void> updateProjectData(final Project project, final String maqlDml) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        final UriResponse uriResponse = restTemplate.postForObject(
                MaqlDml.URI, new MaqlDml(maqlDml), UriResponse.class, project.getId());

        final String errorMessage = format("Unable to update data for project '%s'", project.getId());

        return new PollResult<>(this,
                new AbstractPollHandler<TaskState, Void>(
                        notNullState(uriResponse, "created update project task").getUri(),
                        TaskState.class, Void.class) {
            @Override
            public void handlePollResult(final TaskState pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new GoodDataException(errorMessage);
                }
                setResult(null);
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                final TaskState taskState = extractData(response, TaskState.class);
                if (taskState.isSuccess()) {
                    return true;
                } else if (!taskState.isFinished()) {
                    return false;
                }
                throw new GoodDataException(errorMessage + ": " + taskState.getMessage());
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException(errorMessage + ": " + getPollingUri(), e);
            }
        });
    }

    /**
     * Lists all uploads for the dataset with the given identifier in the given project. Returns empty list if there
     * are no uploads for the given dataset.
     *
     * @param project GoodData project
     * @param datasetId dataset identifier
     * @return collection of {@link Upload} objects or empty list
     */
    public Collection<Upload> listUploadsForDataset(Project project, String datasetId) {
        final UploadsInfo.DataSet dataSet = getDataSetInfo(project, datasetId);

        if (isEmpty(dataSet.getUploadsUri())) {
            return emptyList();
        }

        try {
            final Uploads result = restTemplate.getForObject(dataSet.getUploadsUri(), Uploads.class);

            if (result == null) {
                throw new GoodDataException("Empty response from '" + dataSet.getUploadsUri() + "'.");
            } else if (result.items() == null){
                return emptyList();
            }

            return result.items();
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get '" + dataSet.getUploadsUri() + "'.", e);
        }
    }

    /**
     * Returns last upload for the dataset with given identifier in the given project. Returns null if the last upload
     * doesn't exist.
     *
     * @param project GoodData project
     * @param datasetId dataset identifier
     * @return last dataset upload or {@code null} if the upload doesn't exist
     */
    public Upload getLastUploadForDataset(Project project, String datasetId) {
        final UploadsInfo.DataSet dataSet = getDataSetInfo(project, datasetId);

        if (isEmpty(dataSet.getLastUploadUri())) {
            return null;
        }

        try {
            return restTemplate.getForObject(dataSet.getLastUploadUri(), Upload.class);
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get '" + dataSet.getLastUploadUri() + "'.");
        }
    }

    /**
     * Returns global upload statistics for the given project.
     *
     * @param project GoodData project
     * @return {@link UploadStatistics} object with project's upload statistics
     */
    public UploadStatistics getUploadStatistics(Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            return restTemplate.getForObject(UploadStatistics.URI, UploadStatistics.class, project.getId());
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get dataset uploads statistics.", e);
        }
    }

    /**
     * Returns {@link UploadsInfo.DataSet} object containing upload information for the given dataset in the given project.
     *
     * Package-private for testing purposes.
     */
    UploadsInfo.DataSet getDataSetInfo(Project project, String datasetId) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notEmpty(datasetId, "datasetId");

        final URI uploadsInfoUri = UPLOADS_INFO_TEMPLATE.expand(project.getId());
        try {
            final UploadsInfo uploadsInfo = restTemplate.getForObject(uploadsInfoUri, UploadsInfo.class);
            if (uploadsInfo == null) {
                throw new GoodDataException("Empty response from '" + uploadsInfoUri.toString() + "'.");
            }

            return uploadsInfo.getDataSet(datasetId);
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get '" + uploadsInfoUri.toString() + "'.", e);
        }
    }
}
