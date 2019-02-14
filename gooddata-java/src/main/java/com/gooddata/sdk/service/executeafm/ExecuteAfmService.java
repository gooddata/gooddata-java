/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm;

import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.PollResult;
import com.gooddata.sdk.service.SimplePollHandler;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.ResultPage;
import com.gooddata.sdk.model.executeafm.VisualizationExecution;
import com.gooddata.sdk.model.executeafm.response.ExecutionResponse;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import com.gooddata.sdk.model.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.gooddata.util.Validate.notNull;

/**
 * Service for executing reports with new visualization structures like AFM or Visualization Object.
 * This includes GD API endpoints:
 * <ul>
 *     <li>{@code /executeAfm}</li>
 *     <li>{@code /executeVisualization}</li>
 * </ul>
 */
public class ExecuteAfmService extends AbstractService {

    private static final String AFM_EXECUTION_URI = "/gdc/app/projects/{projectId}/executeAfm";
    private static final String VISUALIZATION_EXECUTION_URI = "/gdc/app/projects/{projectId}/executeVisualization";
    private static final String RESULT_OFFSET = "offset";
    private static final String RESULT_LIMIT = "limit";

    /**
     * Constructor.
     *
     * @param restTemplate rest template
     * @param settings     settings
     */
    public ExecuteAfmService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Executes the given AFM execution returning the execution response
     * @param project project of the execution
     * @param execution execution
     * @return response of the submitted execution
     */
    public ExecutionResponse executeAfm(final Project project, final Execution execution) {
        final String projectId = notNull(notNull(project, "project").getId(), "projectId");
        final ExecutionResponse response;
        try {
            response = restTemplate.postForObject(
                    AFM_EXECUTION_URI,
                    notNull(execution, "execution"),
                    ExecutionResponse.class,
                    projectId);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to execute AFM", e);
        }

        if (response == null) {
            throw new GoodDataException("Empty response when execution posted to API");
        }

        return response;
    }

    /**
     * Executes the given execution returning the execution response
     * @param project project of the execution
     * @param execution execution
     * @return response of the submitted execution
     */
    public ExecutionResponse executeVisualization(final Project project, final VisualizationExecution execution) {
        final String projectId = notNull(notNull(project, "project").getId(), "projectId");
        final ExecutionResponse response;
        try {
            response = restTemplate.postForObject(
                    VISUALIZATION_EXECUTION_URI,
                    notNull(execution, "execution"),
                    ExecutionResponse.class,
                    projectId);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to execute visualization", e);
        }

        if (response == null) {
            throw new GoodDataException("Empty response when execution posted to API");
        }

        return response;
    }

    /**
     * Get for result of given response.
     * @param executionResponse response to get the result
     * @return future of execution result
     */
    public FutureResult<ExecutionResult> getResult(final ExecutionResponse executionResponse) {
        return new PollResult<>(this, new ExecutionPollHandler(notNull(executionResponse, "executionResponse").getExecutionResultUri()));
    }

    /**
     * Get for page of result of given response.
     * @param executionResponse response to get the result
     * @param page desired result page specification
     * @return future of execution result
     */
    public FutureResult<ExecutionResult> getResult(final ExecutionResponse executionResponse, final ResultPage page) {
        final String executionResultUri = notNull(executionResponse, "executionResponse").getExecutionResultUri();

        final String pagedResultUri = UriComponentsBuilder.fromUriString(executionResultUri)
                .replaceQueryParam(RESULT_OFFSET, page.getOffsetsQueryParam())
                .replaceQueryParam(RESULT_LIMIT, page.getLimitsQueryParam())
                .build(true)
                .toUriString();

        return new PollResult<>(this, new ExecutionPollHandler(pagedResultUri));
    }

    private static final class ExecutionPollHandler extends SimplePollHandler<ExecutionResult> {

        ExecutionPollHandler(String pollingUri) {
            super(pollingUri, ExecutionResult.class);
        }

        @Override
        public void handlePollException(GoodDataRestException e) {
            throw new ExecutionResultException(e);
        }
    }
}
