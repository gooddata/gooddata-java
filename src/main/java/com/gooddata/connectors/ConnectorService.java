/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connectors;

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
 * Connector Service
 */
public class ConnectorService extends AbstractService {

    private final ProjectService projectService;

    public ConnectorService(final RestTemplate restTemplate, final ProjectService projectService) {
        super(restTemplate);
        this.projectService = notNull(projectService, "projectService");
    }

    /**
     * Create connector integration with given settings
     * @param project project
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
        final Integration integration = createIntegration(project, settings.getConnector(), new Integration(template.getUrl()));
        updateSettings(project, settings);
        return integration;
    }

    /**
     * Create connector integration
     * @param project project
     * @param connector connector
     * @param integration integration
     * @return created integration
     * @throws ConnectorException if integration can't be created
     */
    public Integration createIntegration(final Project project, final Connector connector, final Integration integration) {
        notNull(project, "project");
        notNull(connector, "connector");
        notNull(integration, "integration");
        try {
            return restTemplate.postForObject(Integration.URL, integration, Integration.class, project.getId(), connector.getName());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to create " + connector + " integration", e);
        }
    }

    /**
     * Update integration settings
     * @param project project
     * @param settings integration settings
     * @throws ConnectorException if settings can't be updated
     */
    public <T extends Settings> void updateSettings(final Project project, final T settings) {
        notNull(settings, "settings");
        notNull(project, "project");
        try {
            restTemplate.put(Settings.URL, settings, project.getId(), settings.getConnector().getName());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to set " + settings.getConnector() + " settings", e);
        }
    }

    /**
     * Execute connector process
     * @param project project
     * @param execution process execution
     * @return executed process
     * @throws ConnectorException if process execution fails
     */
    public FutureResult<Process> executeProcess(final Project project, final ProcessExecution execution) {
        notNull(project, "project");
        notNull(execution, "execution");
        final String connectorName = execution.getConnector().getName();
        try {
            final UriResponse response = restTemplate.postForObject(Process.URL, execution, UriResponse.class, project.getId(), connectorName);
            return new FutureResult<>(this, new SimplePollHandler<Process>(response.getUri(), Process.class) {
                @Override
                public boolean isFinished(final ClientHttpResponse response) throws IOException {
                    final Process process = extractData(response, Process.class);
                    return process.isFinished();
                }

                @Override
                public void handlePollResult(final Process pollResult) {
                    super.handlePollResult(pollResult);
                    if (pollResult.isFailed()) {
                        throw new ConnectorException("Unable to execute " + connectorName + " process: " +
                                pollResult.getStatus().getDescription());
                    }
                }
            });
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to execute " + connectorName + " process", e);
        }
    }
}
