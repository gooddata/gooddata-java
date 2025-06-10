/*
 * (C) 2023 GoodData Corporation.
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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.*;

public class DatasetService extends AbstractService {

    public static final UriTemplate UPLOADS_INFO_TEMPLATE = new UriTemplate(UploadsInfo.URI);
    private static final String MANIFEST_FILE_NAME = "upload_info.json";
    private static final String ETL_PULL_DEFAULT_ERROR_MESSAGE = "ETL Pull failed with status %s";

    private final DataStoreService dataStoreService;

    public DatasetService(final WebClient webClient, final DataStoreService dataStoreService, final GoodDataSettings settings) {
        super(webClient, settings);
        this.dataStoreService = dataStoreService;
    }

    /**
     * Obtains manifest for the given dataset in the specified project.
     */
    public DatasetManifest getDatasetManifest(Project project, String datasetId) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notEmpty(datasetId, "datasetId");
        try {
            String uri = DatasetManifest.URI.replace("{projectId}", project.getId()).replace("{datasetId}", datasetId);
            DatasetManifest manifest = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(DatasetManifest.class)
                    .block();
            if (manifest == null) {
                throw new DatasetNotFoundException(datasetId, null);
            }
            return manifest;
        } catch (GoodDataRestException e) {
            if (e.getStatusCode() == 404) {
                throw new DatasetNotFoundException(datasetId, e);
            } else {
                throw new DatasetException("Unable to get manifest", datasetId, e);
            }
        } catch (Exception e) {
            throw new DatasetException("Unable to get manifest", datasetId, e);
        }
    }

    /**
     * Loads a dataset into the platform: uploads the dataset and manifest to staging and triggers ETL pull.
     */
    public FutureResult<Void> loadDataset(final Project project, final DatasetManifest manifest, final InputStream dataset) {
        notNull(project, "project");
        notNull(dataset, "dataset");
        notNull(manifest, "manifest");
        manifest.setSource(dataset);

        return loadDatasets(project, manifest);
    }

    public FutureResult<Void> loadDataset(Project project, String datasetId, InputStream dataset) {
        notNull(project, "project");
        notEmpty(datasetId, "datasetId");
        notNull(dataset, "dataset");
        return loadDataset(project, getDatasetManifest(project, datasetId), dataset);
    }

    public FutureResult<Void> loadDatasets(final Project project, DatasetManifest... datasets) {
        return loadDatasets(project, List.of(datasets));
    }

    /**
     * Loads multiple datasets and their manifests to staging, then triggers ETL pull.
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
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(manifestJson.getBytes(StandardCharsets.UTF_8));
            dataStoreService.upload(dirPath + MANIFEST_FILE_NAME, inputStream);

            return pullLoad(project, dirPath, datasetsNames);
        } catch (IOException e) {
            throw new DatasetException("Unable to serialize manifest", datasetsNames, e);
        } catch (DataStoreException | GoodDataRestException e) {
            throw new DatasetException("Unable to load", datasetsNames, e);
        }
    }

    private void validateUploadManifests(final Collection<DatasetManifest> datasets) {
        notEmpty(datasets, "datasets");
        for (DatasetManifest datasetManifest : datasets) {
            if (datasetManifest.getSource() == null) {
                throw new IllegalArgumentException(String.format("Source for dataset '%s' is null", datasetManifest.getDataSet()));
            }
            if (datasetManifest.getFile() == null) {
                throw new IllegalArgumentException(String.format("File for dataset '%s' is null", datasetManifest.getDataSet()));
            }
            if (org.springframework.util.StringUtils.isEmpty(datasetManifest.getDataSet())) {
                throw new IllegalArgumentException("Dataset name is empty.");
            }
        }
    }

    private FutureResult<Void> pullLoad(Project project, final String dirPath, final Collection<String> datasets) {
        notNull(project.getId(), "project.id");

        // Trigger ETL pull via WebClient POST
        PullTask pullTask = webClient.post()
                .uri(Pull.URI.replace("{projectId}", project.getId()))
                .bodyValue(new Pull(dirPath))
                .retrieve()
                .bodyToMono(PullTask.class)
                .block();

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, Void>(
                notNullState(pullTask, "created pull task").getPollUri(), TaskStatus.class, Void.class) {
            @Override
            public void handlePollResult(TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    final String message = org.springframework.util.StringUtils.isEmpty(pollResult.getMessages())
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
                    // TODO: log error
                }
            }
        });
    }

    /**
     * Lists all datasets (links) in a project. Returns empty list if there are no datasets.
     */
    public Collection<Link> listDatasetLinks(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        try {
            String uri = DatasetLinks.URI.replace("{projectId}", project.getId());
            DatasetLinks result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(DatasetLinks.class)
                    .block();
            if (result == null) {
                throw new GoodDataException("Empty response from API call");
            } else if (result.getLinks() == null) {
                return java.util.Collections.emptyList();
            }
            return result.getLinks();
        } catch (Exception e) {
            throw new GoodDataException("Unable to list datasets for project " + project.getId(), e);
        }
    }

    /**
     * Optimize SLI hash for a project.
     */
    public FutureResult<Void> optimizeSliHash(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        UriResponse uriResponse = webClient.post()
                .uri(EtlMode.URL.replace("{projectId}", project.getId()))
                .bodyValue(new EtlMode(EtlModeType.SLI, LookupMode.RECREATE))
                .retrieve()
                .bodyToMono(UriResponse.class)
                .block();

        final String errorMessage = String.format("Unable to optimize SLI hash for project '%s'", project.getId());

        return new PollResult<>(this,
                new AbstractPollHandler<TaskStatus, Void>(
                        notNullState(uriResponse, "created optimize task").getUri(),
                        TaskStatus.class, Void.class) {
            @Override
            public void handlePollResult(final TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new GoodDataException(errorMessage);
                }
                setResult(null);
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException(errorMessage, e);
            }
        });
    }

    /**
     * Update project data with the given MAQL script.
     */
    public FutureResult<Void> updateProjectData(final Project project, final String maqlDml) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        UriResponse uriResponse = webClient.post()
                .uri(MaqlDml.URI.replace("{projectId}", project.getId()))
                .bodyValue(new MaqlDml(maqlDml))
                .retrieve()
                .bodyToMono(UriResponse.class)
                .block();

        final String errorMessage = String.format("Unable to update data for project '%s'", project.getId());

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
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException(errorMessage, e);
            }
        });
    }

    /**
     * Lists all uploads for a dataset in a project.
     */
    public Collection<Upload> listUploadsForDataset(Project project, String datasetId) {
        final UploadsInfo.DataSet dataSet = getDataSetInfo(project, datasetId);

        if (org.springframework.util.StringUtils.isEmpty(dataSet.getUploadsUri())) {
            return java.util.Collections.emptyList();
        }

        try {
            Uploads result = webClient.get()
                    .uri(dataSet.getUploadsUri())
                    .retrieve()
                    .bodyToMono(Uploads.class)
                    .block();

            if (result == null) {
                throw new GoodDataException("Empty response from '" + dataSet.getUploadsUri() + "'.");
            } else if (result.items() == null){
                return java.util.Collections.emptyList();
            }

            return result.items();
        } catch (Exception e) {
            throw new GoodDataException("Unable to get '" + dataSet.getUploadsUri() + "'.", e);
        }
    }

    /**
     * Returns the last upload for a dataset in a project, or null if none exists.
     */
    public Upload getLastUploadForDataset(Project project, String datasetId) {
        final UploadsInfo.DataSet dataSet = getDataSetInfo(project, datasetId);

        if (!org.springframework.util.StringUtils.hasLength(dataSet.getLastUploadUri())) {
            return null;
        }
        
        try {
            return webClient.get()
                    .uri(dataSet.getLastUploadUri())
                    .retrieve()
                    .bodyToMono(Upload.class)
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to get '" + dataSet.getLastUploadUri() + "'.");
        }
    }

    /**
     * Returns upload statistics for the project.
     */
    public UploadStatistics getUploadStatistics(Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            String uri = UploadStatistics.URI.replace("{projectId}", project.getId());
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(UploadStatistics.class)
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to get dataset uploads statistics.", e);
        }
    }

    /**
     * Returns DataSet information for the given dataset and project.
     */
    protected UploadsInfo.DataSet getDataSetInfo(Project project, String datasetId) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notEmpty(datasetId, "datasetId");

        final URI uploadsInfoUri = UPLOADS_INFO_TEMPLATE.expand(project.getId());
        try {
            UploadsInfo uploadsInfo = webClient.get()
                    .uri(uploadsInfoUri)
                    .retrieve()
                    .bodyToMono(UploadsInfo.class)
                    .block();
            if (uploadsInfo == null) {
                throw new GoodDataException("Empty response from '" + uploadsInfoUri.toString() + "'.");
            }

            return uploadsInfo.getDataSet(datasetId);
        } catch (Exception e) {
            throw new GoodDataException("Unable to get '" + uploadsInfoUri.toString() + "'.", e);
        }
    }
}
