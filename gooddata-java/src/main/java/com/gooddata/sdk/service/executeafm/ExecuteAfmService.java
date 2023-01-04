/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.executeafm;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.executeafm.Execution;
import com.gooddata.sdk.model.executeafm.ResultPage;
import com.gooddata.sdk.model.executeafm.VisualizationExecution;
import com.gooddata.sdk.model.executeafm.response.ExecutionResponse;
import com.gooddata.sdk.model.executeafm.result.ExecutionResult;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Service for executing reports with new visualization structures like AFM or Visualization Object.
 * This includes GD API endpoints:
 * <ul>
 *     <li>{@code /executeAfm}</li>
 *     <li>{@code /executeVisualization}</li>
 * </ul>
 */
public class ExecuteAfmService extends AbstractService {

    /**
     * URI string containing template link to the {@code /executeAfm} resource.
     */
    public static final String AFM_EXECUTION_URI = "/gdc/app/projects/{projectId}/executeAfm";
    /**
     * URI string containing template link to the {@code /executeVisualization} resource.
     */
    public static final String VISUALIZATION_EXECUTION_URI = "/gdc/app/projects/{projectId}/executeVisualization";
    /**
     * Query param representing offset of result data returned by AFM execution.
     * Can be used for offset query param value replacement in {@link ExecutionResponse#getExecutionResultUri()} link.
     */
    public static final String RESULT_OFFSET = "offset";
    /**
     * Query param representing limit of result data returned by AFM execution.
     * Can be used for limit query param value replacement in {@link ExecutionResponse#getExecutionResultUri()} link.
     */
    public static final String RESULT_LIMIT = "limit";

    /**
     * Constructor.
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
