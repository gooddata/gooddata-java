/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import com.gooddata.project.ProjectTemplate;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;

import static com.gooddata.Validate.notNull;

/**
 * Service for connector integration creation, update of its settings or execution of its process.
 */
public class ConnectorService extends AbstractService {

    private final ProjectService projectService;

    public ConnectorService(final RestTemplate restTemplate, final ProjectService projectService) {
        super(restTemplate);
        this.projectService = notNull(projectService, "projectService");
    }

    /**
     * Create connector integration with given settings
     *
     * @param project  project
     * @param settings integration settings
     * @return created integration
     * @throws ConnectorException if integration can't be created
     */
    public Integration createIntegration(final Project project, final Settings settings) {
        notNull(project, "project");
        notNull(settings, "settings");

        final Collection<ProjectTemplate> projectTemplates = projectService.getProjectTemplates(project);
        if (projectTemplates == null || projectTemplates.isEmpty()) {
            throw new GoodDataException("Project " + project.getId() + " doesn't contain a template reference");
        }
        final ProjectTemplate template = notNull(projectTemplates.iterator().next(), "project template");
        final Integration integration = createIntegration(project, settings.getConnectorType(),
                new Integration(template.getUrl()));
        updateSettings(project, settings);
        return integration;
    }

    /**
     * Create connector integration
     *
     * @param project       project
     * @param connectorType connector type
     * @param integration   integration
     * @return created integration
     * @throws ConnectorException if integration can't be created
     */
    public Integration createIntegration(final Project project, final ConnectorType connectorType,
                                         final Integration integration) {
        notNull(project, "project");
        notNull(connectorType, "connector");
        notNull(integration, "integration");

        try {
            return restTemplate.postForObject(Integration.URL, integration, Integration.class, project.getId(),
                    connectorType.getName());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to create " + connectorType + " integration", e);
        }
    }

    /**
     * Update integration settings
     *
     * @param project  project
     * @param settings integration settings
     * @throws ConnectorException if settings can't be updated
     */
    public void updateSettings(final Project project, final Settings settings) {
        notNull(settings, "settings");
        notNull(project, "project");

        try {
            restTemplate.put(Settings.URL, settings, project.getId(), settings.getConnectorType().getName());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to set " + settings.getConnectorType() + " settings", e);
        }
    }

    /**
     * Execute connector process
     *
     * @param project   project
     * @param execution process execution
     * @return executed process
     * @throws ConnectorException if process execution fails
     */
    public FutureResult<ProcessStatus> executeProcess(final Project project, final ProcessExecution execution) {
        notNull(project, "project");
        notNull(execution, "execution");

        final String connectorType = execution.getConnectorType().getName();
        try {
            final UriResponse response = restTemplate
                    .postForObject(ProcessStatus.URL, execution, UriResponse.class, project.getId(), connectorType);
            return new FutureResult<>(this, new SimplePollHandler<ProcessStatus>(response.getUri(), ProcessStatus.class) {
                @Override
                public boolean isFinished(final ClientHttpResponse response) throws IOException {
                    final ProcessStatus process = extractData(response, ProcessStatus.class);
                    return process.isFinished();
                }

                @Override
                public void handlePollResult(final ProcessStatus pollResult) {
                    super.handlePollResult(pollResult);
                    if (pollResult.isFailed()) {
                        throw new ConnectorException(connectorType + " process failed: " +
                                pollResult.getStatus().getDescription());
                    }
                }
            });
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to execute " + connectorType + " process", e);
        }
    }
}
