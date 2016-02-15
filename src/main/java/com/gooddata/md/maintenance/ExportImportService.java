/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.maintenance;

import static com.gooddata.util.Validate.notNull;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.PollResult;
import com.gooddata.gdc.TaskStatus;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service providing metadata export/import tasks.
 */
public class ExportImportService extends AbstractService {

    public ExportImportService(RestTemplate restTemplate) {
        super(restTemplate);
    }


    /**
     * Exports partial metadata from project and returns token identifying this export
     *
     * @param project project from which metadata should be exported
     * @param export export to execute
     * @return {@link FutureResult} of the task containing token identifying partial export after the task is completed
     * @throws ExportImportException when export resource call fails, polling on export status fails or export status is ERROR
     */
    public FutureResult<PartialMdExportToken> partialExport(Project project, final PartialMdExport export) {
        notNull(project, "project");
        notNull(export, "export");

        final PartialMdArtifact partialMdArtifact;
        try {
            partialMdArtifact = restTemplate.postForObject(PartialMdExport.URI, export, PartialMdArtifact.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException("Unable to export metadata from objects " + export.getUris() + ".", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, PartialMdExportToken>(partialMdArtifact.getStatus().getUri(),
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
        notNull(mdExportToken, "mdExportToken");

        final UriResponse importResponse;
        try {
            importResponse = restTemplate.postForObject(PartialMdExportToken.URI, mdExportToken, UriResponse.class, project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ExportImportException("Unable to import partial metadata to project '" + project.getUri()
                    + "' with token '" + mdExportToken.getToken() + "'.", e);
        }

        return new PollResult<>(this, new AbstractPollHandler<TaskStatus, Void>(importResponse.getUri(), TaskStatus.class, Void.class) {
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
}
