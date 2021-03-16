/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project.model;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.model.DiffRequest;
import com.gooddata.sdk.model.project.model.MaqlDdl;
import com.gooddata.sdk.model.project.model.MaqlDdlLinks;
import com.gooddata.sdk.model.project.model.ModelDiff;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.dataset.DatasetService;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;

import static com.gooddata.sdk.common.util.Validate.notNullState;
import static com.gooddata.sdk.model.project.model.ModelDiff.UpdateScript;
import static com.gooddata.sdk.common.util.Validate.noNullElements;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * Service for manipulating with project model
 */
public class ModelService extends AbstractService {

    public ModelService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    private FutureResult<ModelDiff> getProjectModelDiff(Project project, DiffRequest diffRequest) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(diffRequest, "diffRequest");
        try {
            final AsyncTask asyncTask = restTemplate
                    .postForObject(DiffRequest.URI, diffRequest, AsyncTask.class, project.getId());
            return new PollResult<>(this, new SimplePollHandler<ModelDiff>(notNullState(asyncTask, "model diff task").getUri(), ModelDiff.class) {
                @Override
                public void handlePollException(final GoodDataRestException e) {
                    throw new ModelException("Unable to get project model diff", e);
                }
            });
        } catch (GoodDataRestException | RestClientException e) {
            throw new ModelException("Unable to get project model diff", e);
        }
    }

    public FutureResult<ModelDiff> getProjectModelDiff(Project project, String targetModel) {
        notNull(project, "project");
        notNull(targetModel, "targetModel");
        return getProjectModelDiff(project, new DiffRequest(targetModel));
    }

    public FutureResult<ModelDiff> getProjectModelDiff(Project project, Reader targetModel) {
        notNull(project, "project");
        notNull(targetModel, "targetModel");
        try {
            return getProjectModelDiff(project, FileCopyUtils.copyToString(targetModel));
        } catch (IOException e) {
            throw new ModelException("Can't read target model", e);
        }
    }

    /**
     * Update project model with the MAQL script from given ModelDiff with the least side-effects
     * (see {@link ModelDiff#getUpdateMaql()}).
     *
     * @param project   project to be updated
     * @param modelDiff difference of model to be applied into the project
     * @return poll result
     */
    public FutureResult<Void> updateProjectModel(Project project, ModelDiff modelDiff) {
        notNull(modelDiff, "modelDiff");
        return updateProjectModel(project, modelDiff.getUpdateMaql());
    }

    /**
     * Update project model with the given update script (MAQL).
     *
     * @param project      project to be updated
     * @param updateScript update script to be executed in the project
     * @return poll result
     */
    public FutureResult<Void> updateProjectModel(Project project, UpdateScript updateScript) {
        notNull(updateScript, "updateScript");
        return updateProjectModel(project, updateScript.getMaqlChunks());
    }

    /**
     * Update project model with the given update script(s) (MAQL). For data manipulation use
     * {@link ModelService#updateProjectModel}.
     *
     * @param project project to be updated
     * @param maqlDdl update script to be executed in the project
     * @return poll result
     *
     * @see DatasetService#updateProjectData
     */
    public FutureResult<Void> updateProjectModel(final Project project, final String... maqlDdl) {
        return updateProjectModel(project, asList(maqlDdl));
    }

    /**
     * Update project model with the given update script(s) (MAQL).
     *
     * @param project project to be updated
     * @param maqlDdl update script to be executed in the project
     * @return poll result
     *
     * @see DatasetService#updateProjectData
     */
    public FutureResult<Void> updateProjectModel(final Project project, final Collection<String> maqlDdl) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        noNullElements(maqlDdl, "maqlDdl");
        if (maqlDdl.isEmpty()) {
            throw new IllegalArgumentException("MAQL DDL string(s) should be given");
        }
        return new PollResult<>(this, new AbstractPollHandlerBase<MaqlDdlLinks, Void>(MaqlDdlLinks.class, Void.class) {

            private final String projectId = project.getId();
            private final LinkedList<String> maqlChunks = new LinkedList<>(maqlDdl);
            private String pollUri;

            {
                executeNextMaqlChunk();
            }

            /**
             * @return true if polling should finish, false otherwise
             */
            private boolean executeNextMaqlChunk() {
                if (maqlChunks.isEmpty()) {
                    return true;
                }
                try {
                    final MaqlDdlLinks links = restTemplate.postForObject(MaqlDdl.URI, new MaqlDdl(maqlChunks.poll()),
                        MaqlDdlLinks.class, projectId);
                    this.pollUri = notNullState(links, "maqlDdlLinks").getStatusUri();
                } catch (GoodDataRestException | RestClientException e) {
                    throw new ModelException("Unable to update project model", e);
                }
                return false;
            }

            @Override
            public String getPollingUri() {
                return pollUri;
            }

            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                if (!super.isFinished(response)) {
                    return false;
                }
                final TaskStatus maqlDdlTaskStatus = extractData(response, TaskStatus.class);
                if (!maqlDdlTaskStatus.isSuccess()) {
                    throw new ModelException("Unable to update project model: " + maqlDdlTaskStatus.getMessages());
                }
                return executeNextMaqlChunk();
            }

            @Override
            public void handlePollResult(MaqlDdlLinks pollResult) {
                setResult(null);
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ModelException("Unable to update project model", e);
            }
        });
    }

}
