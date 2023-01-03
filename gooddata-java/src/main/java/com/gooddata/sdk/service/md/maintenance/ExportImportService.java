/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.dataset.TaskState;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.maintenance.*;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static java.lang.String.format;

/**
 * Service providing metadata export/import tasks.
 */
public class ExportImportService extends AbstractService {

    public ExportImportService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Exports partial metadata from project and returns token identifying this export
     *
     * @param project project from which metadata should be exported
     * @param export export configuration to execute
     * @return {@link FutureResult} of the task containing token identifying partial export after the task is completed
     * @throws ExportImportException when export resource call fails, polling on export status fails or export status is ERROR
     */
    public FutureResult<PartialMdExportToken> partialExport(Project project, final PartialMdExport export) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(export, "export");

        final PartialMdArtifact partialMdArtifact;
        try {
            partialMdArtifact = restTemplate.postForObject(PartialMdExport.URI, export, PartialMdArtifact.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException("Unable to export metadata from objects " + export.getUris() + ".", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, PartialMdExportToken>(
                notNullState(partialMdArtifact, "partial export response").getStatusUri(),
                TaskStatus.class, PartialMdExportToken.class) {
            @Override
            public void handlePollResult(TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new ExportImportException("Partial metadata export failed with errors: " + pollResult.getMessages());
                }
                setResult(new PartialMdExportToken(partialMdArtifact.getToken(), export.isExportAttributeProperties()));
            }

            @Override
            public void handlePollException(GoodDataRestException e) {
                throw new ExportImportException("Unable to to export partial metadata.", e);
            }
        });
    }

    /**
     * Imports partial metadata to project based on given token
     *
     * @param project project to which metadata should be imported
     * @param mdExportToken export token to be imported
     * @return {@link FutureResult} of the task
     * @throws ExportImportException when import resource call fails, polling on import status fails or import status is ERROR
     */
    public FutureResult<Void> partialImport(Project project, PartialMdExportToken mdExportToken) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(mdExportToken, "mdExportToken");

        final UriResponse importResponse;
        try {
            importResponse = restTemplate.postForObject(PartialMdExportToken.URI, mdExportToken, UriResponse.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException("Unable to import partial metadata to project '" + project.getId()
                    + "' with token '" + mdExportToken.getToken() + "'.", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, Void>(notNullState(importResponse, "partial import response").getUri(),
                TaskStatus.class, Void.class) {
            @Override
            public void handlePollResult(TaskStatus pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new ExportImportException("Partial metadata import failed with errors: " + pollResult.getMessages());
                }
                setResult(null);
            }

            @Override
            public void handlePollException(GoodDataRestException e) {
                throw new ExportImportException("Unable to import partial metadata.", e);
            }
        });
    }

    /**
     * Exports complete project and returns token identifying this export
     *
     * @param project project from which metadata should be exported
     * @param export  export configuration to execute
     * @return {@link FutureResult} of the task containing token identifying export after the task is completed
     * @throws ExportImportException when export resource call fails, polling on export status fails or export status is
     *                               ERROR
     */
    public FutureResult<ExportProjectToken> exportProject(final Project project, final ExportProject export) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(export, "export");

        final ExportProjectArtifact exportProjectArtifact;
        final String errorMessage = format("Unable to export complete project '%s'", project.getId());
        try {
            exportProjectArtifact = restTemplate
                    .postForObject(ExportProject.URI, export, ExportProjectArtifact.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException(errorMessage, e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskState, ExportProjectToken>(
                notNullState(exportProjectArtifact, "export response").getStatusUri(),
                TaskState.class, ExportProjectToken.class) {

            @Override
            public void handlePollResult(TaskState pollResult) {
                if (!pollResult.isSuccess()) {
                    throw new ExportImportException(errorMessage + ": " + pollResult.getMessage());
                }
                setResult(new ExportProjectToken(exportProjectArtifact.getToken()));
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                final TaskState taskState = extractData(response, TaskState.class);
                if (taskState != null && taskState.isSuccess()) {
                    return true;
                } else if (taskState == null || !taskState.isFinished()) {
                    return false;
                }
                throw new ExportImportException(errorMessage + ": " + taskState.getMessage());
            }

            @Override
            public void handlePollException(GoodDataRestException e) {
                throw new ExportImportException(errorMessage + ": " + e.getText(), e);
            }
        });
    }

    /**
     * Imports complete project based on given token
     *
     * @param project project to which metadata should be imported
     * @param exportToken export token to be imported
     * @return {@link FutureResult} of the task
     * @throws ExportImportException when import resource call fails, polling on import status fails or import status is ERROR
     */
    public FutureResult<Void> importProject(final Project project, final ExportProjectToken exportToken) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(exportToken, "exportToken");

        final UriResponse importResponse;
        final String errorMessage = format("Unable to import complete project into '%s' with token '%s'",
                project.getId(), exportToken.getToken());
        try {
            importResponse = restTemplate.postForObject(ExportProjectToken.URI, exportToken, UriResponse.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException(errorMessage, e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskState, Void>(
                notNullState(importResponse, "project import response").getUri(),
                TaskState.class, Void.class) {
            @Override
            public void handlePollResult(TaskState pollResult) {
                if (pollResult.isFinished() && !pollResult.isSuccess()) {
                    throw new ExportImportException(errorMessage + ": " + pollResult.getMessage());
                }
                setResult(null);
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                final TaskState taskState = extractData(response, TaskState.class);
                if (taskState != null && taskState.isSuccess()) {
                    return true;
                } else if (taskState == null || !taskState.isFinished()) {
                    return false;
                }
                throw new ExportImportException(errorMessage + ": " + taskState.getMessage());
            }

            @Override
            public void handlePollException(GoodDataRestException e) {
                throw new ExportImportException(errorMessage + ": " + e.getText(), e);
            }
        });
    }
}
