/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.PollResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.service.SimplePollHandler;
import com.gooddata.sdk.model.connector.*;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.service.project.ProjectService;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.ProjectTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

/**
 * Service for connector integration creation, update of its settings or execution of its process.
 */
public class ConnectorService extends AbstractService {

    private final ProjectService projectService;

    public ConnectorService(final RestTemplate restTemplate, final ProjectService projectService, final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.projectService = notNull(projectService, "projectService");
    }

    /**
     * @deprecated use ConnectorService(RestTemplate, ProjectService, GoodDataSettings) constructor instead
     */
    @Deprecated
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
        notNull(project.getId(), "project.id");
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
        notNull(project.getId(), "project.id");
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
        notNull(project.getId(), "project.id");
        notNull(connectorType, "connectorType");
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
        notNull(project.getId(), "project.id");
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
     * Gets settings for coupa connector.
     * @param project project
     * @return settings for coupa connector
     */
    public CoupaSettings getCoupaSettings(Project project) {
        return getSettings(project, ConnectorType.COUPA, CoupaSettings.class);
    }

    /**
     * Gets settings for pardot connector.
     * @param project project
     * @return settings for pardot connector
     */
    public PardotSettings getPardotSettings(Project project) {
        return getSettings(project, ConnectorType.PARDOT, PardotSettings.class);
    }

    /**
     * Creates Coupa connector instance.
     *
     * @param project project
     * @param instance instance with it's API URL, API key and name
     * @return created Coupa instance
     */
    public CoupaInstance createCoupaInstance(Project project, CoupaInstance instance) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(instance, "instance");

        //POST request for creating Coupa instance returns created instance URI as response
        final UriResponse instanceUri;
        try {
            instanceUri = restTemplate.postForObject(CoupaInstances.URL, instance, UriResponse.class,
                    project.getId());
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to create Coupa instance with API URL '" + instance.getApiUrl() +
                    "'", e);
        }

        try {
            return restTemplate.getForObject(instanceUri.getUri(), CoupaInstance.class);
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to get created Coupa instance with URI '" + instanceUri.getUri() +
                    "'", e);
        }
    }

    /**
     * Returns collection of all existing Coupa instances
     *
     * @param project project
     * @return collection of Coupa instances or empty collection if no Coupa instances are defined
     */
    public Collection<CoupaInstance> findCoupaInstances(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            final CoupaInstances instances =
                    restTemplate.getForObject(CoupaInstances.URL, CoupaInstances.class, project.getId());

            if (instances == null) {
                throw new ConnectorException("Empty response from API call.");
            }
            if (instances.getItems() == null) {
                return emptyList();
            }

            return instances.getItems();
        } catch (GoodDataRestException | RestClientException e) {
            throw new ConnectorException("Unable to get Coupa instances.", e);
        }
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
    public <T extends Settings> T getSettings(final Project project, final ConnectorType connectorType,
                                              final Class<T> settingsClass) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(connectorType, "connectorType");
        notNull(settingsClass, "settingsClass");

        try {
            return restTemplate.getForObject(connectorType.getSettingsUrl(), settingsClass, project.getId());
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
        notNull(settings.getConnectorType(), "settings.connectorType");
        notNull(project.getId(), "project.id");
        notNull(project, "project");

        try {
            restTemplate.put(settings.getConnectorType().getSettingsUrl(), settings, project.getId());
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
        notNull(project.getId(), "project.id");
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

    protected FutureResult<ProcessStatus> createProcessPollResult(final String uri) {
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
