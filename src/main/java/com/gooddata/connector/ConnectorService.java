/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.PollResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import com.gooddata.project.ProjectTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;

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
     * Retrieve connector integration
     *
     * @param project       project
     * @param connectorType connector type
     * @return              integration
     * @throws ConnectorException if integration can't be retrieved
     */
    public Integration getIntegration(final Project project, final ConnectorType connectorType) {
        notNull(project, "project");
        notNull(connectorType, "connector");

        try {
            return restTemplate.getForObject(Integration.URL, Integration.class, project.getId(), connectorType.getName());
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new IntegrationNotFoundException(project, connectorType, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new ConnectorException("Unable to get " + connectorType + " integration", e);
        }
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
     * Update connector integration
     *
     * @param project       project
     * @param connectorType connector type
     * @param integration   integration
     * @throws ConnectorException if integration can't be updated
     */
    public void updateIntegration(final Project project, final ConnectorType connectorType,
                                         final Integration integration) {
        notNull(project, "project");
        notNull(connectorType, "connector");
        notNull(integration, "integration");

        try {
            restTemplate.put(Integration.URL, integration, project.getId(), connectorType.getName());
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new IntegrationNotFoundException(project, connectorType, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new ConnectorException("Unable to update " + connectorType + " integration", e);
        }
    }

    /**
     * Get settings for zendesk4 connector.
     * @param project project
     * @return settings for zendesk4 connector
     */
    public Zendesk4Settings getZendesk4Settings(Project project) {
        return getSettings(project, ConnectorType.ZENDESK4, Zendesk4Settings.class);
    }

    /**
     * Get settings for given connector of given class.
     *
     * @param project project
     * @param connectorType type of connector to fetch settings ofr
     * @param settingsClass class of settings fetched
     * @param <T> type of fetched settings
     * @return settings of connector
     */
    public <T extends Settings> T getSettings(Project project, ConnectorType connectorType, Class<T> settingsClass) {
        notNull(project, "project");
        notNull(connectorType, "connectorType");
        notNull(settingsClass, "settingsClass");

        try {
            return restTemplate.getForObject(Settings.URL, settingsClass, project.getId(), connectorType.getName());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to get " + connectorType + " integration settings", e);
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
            return createProcessPollResult(response.getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to execute " + connectorType + " process", e);
        }
    }


    /**
     * Gets status of provided connector process.
     * <p>
     * You can use process retrieved by <code>getXXXProcess</code> methods on {@link Integration} as well as a result of
     * {@link ConnectorService#executeProcess(Project, ProcessExecution)}.
     *
     * @param process process to be executed
     * @return executed process
     * @throws ConnectorException if process execution fails
     */
    public FutureResult<ProcessStatus> getProcessStatus(final IntegrationProcessStatus process) {
        notNull(process, "process");
        notNull(process.getUri(), "process.getUri");
        return createProcessPollResult(process.getUri());
    }

    private FutureResult<ProcessStatus> createProcessPollResult(final String uri) {
        final Map<String, String> match = IntegrationProcessStatus.TEMPLATE.match(uri);
        final String connectorType = match.get("connector");
        final String processId = match.get("process");
        return new PollResult<>(this, new SimplePollHandler<ProcessStatus>(uri, ProcessStatus.class) {
            @Override
            public boolean isFinished(final ClientHttpResponse response) throws IOException {
                final ProcessStatus process = extractData(response, ProcessStatus.class);
                return process.isFinished();
            }

            @Override
            public void handlePollResult(final ProcessStatus pollResult) {
                super.handlePollResult(pollResult);
                if (pollResult.isFailed()) {
                    throw new ConnectorException(format("%s process %s failed: %s", connectorType, processId,
                            pollResult.getStatus().getDescription()));
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ConnectorException(format("%s process %s failed: %s", connectorType, processId,
                        e.getText()), e);
            }
        });
    }

}
