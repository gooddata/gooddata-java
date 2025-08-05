/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.connector.*;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.ProjectTemplate;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.project.ProjectService;
import org.springframework.web.reactive.function.client.WebClient;
import com.gooddata.sdk.service.retry.RetryableWebClient;
import com.gooddata.sdk.service.retry.GetServerErrorRetryStrategy;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.UriTemplate;

import org.springframework.http.HttpMethod;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static java.lang.String.format;

/**
 * Service for connector integration creation, update of its settings or execution of its process.
 */
public class ConnectorService extends AbstractService {

    public static final UriTemplate STATUS_TEMPLATE = new UriTemplate(IntegrationProcessStatus.URI);
    private final ProjectService projectService;
    private final RetryableWebClient retryableWebClient;

    public ConnectorService(final WebClient webClient, final ProjectService projectService, final GoodDataSettings settings) {
        super(webClient, settings);
        this.projectService = notNull(projectService, "projectService");
        this.retryableWebClient = new RetryableWebClient(webClient, settings.getRetrySettings(), new GetServerErrorRetryStrategy());
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
            String url = Integration.URL.replace("{project}", project.getId())
                                       .replace("{connector}", connectorType.getName());
            
            // For retryable client, we need to construct the full URI
            // Since the retryable client's execute method expects a complete URI,
            // we'll need to use a different approach for now to maintain compatibility
            // Let's use the webClient directly but check if we need retries based on response
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Integration.class)
                    .retryWhen(reactor.util.retry.Retry.backoff(
                        settings.getRetrySettings().getRetryCount(),
                        java.time.Duration.ofMillis(settings.getRetrySettings().getRetryInitialInterval()))
                        .maxBackoff(java.time.Duration.ofMillis(settings.getRetrySettings().getRetryMaxInterval()))
                        .filter(throwable -> {
                            if (throwable instanceof WebClientResponseException) {
                                WebClientResponseException wcre = (WebClientResponseException) throwable;
                                // Only retry for GET operations with server errors (500, 502, 503, 504, 507)
                                return new GetServerErrorRetryStrategy().retryAllowed("GET", wcre.getStatusCode().value(), null);
                            }
                            return false;
                        }))
                    .onErrorMap(WebClientResponseException.class, e -> {
                        if (e.getStatusCode().value() == 404) {
                            return new IntegrationNotFoundException(project, connectorType, e);
                        } else {
                            return new GoodDataRestException(
                                e.getStatusCode().value(),
                                e.getStatusText(),
                                e.getResponseBodyAsString(),
                                url,
                                "WebClient"
                            );
                        }
                    })
                    .block();
        } catch (GoodDataRestException e) {
            // Re-throw GoodDataRestException (including IntegrationNotFoundException) as-is
            throw e;
        } catch (Exception e) {
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
            String url = Integration.URL.replace("{project}", project.getId())
                                       .replace("{connector}", connectorType.getName());
            return webClient.post()
                    .uri(url)
                    .bodyValue(integration)
                    .retrieve()
                    .bodyToMono(Integration.class)
                    .block();
        } catch (Exception e) {
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
            String url = Integration.URL.replace("{project}", project.getId())
                                       .replace("{connector}", connectorType.getName());
            webClient.put()
                    .uri(url)
                    .bodyValue(integration)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new IntegrationNotFoundException(project, connectorType, e);
            } else {
                String url = Integration.URL.replace("{project}", project.getId())
                                           .replace("{connector}", connectorType.getName());
                throw new GoodDataRestException(
                    e.getStatusCode().value(),
                    e.getStatusText(),
                    e.getResponseBodyAsString(),
                    url,
                    "WebClient"
                );
            }
        } catch (Exception e) {
            throw new ConnectorException("Unable to update " + connectorType + " integration", e);
        }
    }

    /**
     * Delete connector integration.
     *
     * @param project       project
     * @param connectorType connector type
     * @throws IntegrationNotFoundException if integration doesn't exist
     * @throws ConnectorException           if integration can't be deleted
     */
    public void deleteIntegration(final Project project, final ConnectorType connectorType) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(connectorType, "connector");

        try {
            String url = Integration.URL.replace("{project}", project.getId())
                                       .replace("{connector}", connectorType.getName());
            webClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new IntegrationNotFoundException(project, connectorType, e);
            } else {
                String url = Integration.URL.replace("{project}", project.getId())
                                           .replace("{connector}", connectorType.getName());
                throw new GoodDataRestException(
                    e.getStatusCode().value(),
                    e.getStatusText(),
                    e.getResponseBodyAsString(),
                    url,
                    "WebClient"
                );
            }
        } catch (Exception e) {
            throw new ConnectorException("Unable to delete " + connectorType + " integration", e);
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
    public <T extends Settings> T getSettings(final Project project, final ConnectorType connectorType,
                                              final Class<T> settingsClass) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(connectorType, "connectorType");
        notNull(settingsClass, "settingsClass");

        try {
            String url = connectorType.getSettingsUrl().replace("{project}", project.getId());
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(settingsClass)
                    .block();
        } catch (Exception e) {
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
            String url = settings.getConnectorType().getSettingsUrl().replace("{project}", project.getId());
            webClient.put()
                    .uri(url)
                    .bodyValue(settings)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
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
            String url = ProcessStatus.URL.replace("{project}", project.getId())
                                         .replace("{connector}", connectorType);
            final UriResponse response = webClient.post()
                    .uri(url)
                    .bodyValue(execution)
                    .retrieve()
                    .bodyToMono(UriResponse.class)
                    .block();
            return createProcessPollResult(notNullState(response, "created process response").getUri());
        } catch (Exception e) {
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

    /**
     * Get Zendesk reload.
     *
     * You should use the result of {@link #scheduleZendesk4Reload} to see changes in {@link Reload#getStatus()} and
     * {@link Reload#getProcessId()} or retrieve process URI {@link Reload#getProcessUri()}.
     *
     * @param reload existing reload (self link must be present).
     * @return same reload with refreshed properties (status, processId, process URI)
     */
    public Reload getZendesk4Reload(final Reload reload) {
        notNull(reload, "reload");
        final Optional<String> reloadUri = reload.getUri();
        if (reloadUri.isPresent()) {
            return getZendesk4ReloadByUri(reloadUri.get());
        } else {
            throw new IllegalArgumentException("Self link in the Reload must be present!");
        }
    }

    /**
     * Get Zendesk reload by URI using WebClient.
     * @param reloadUri existing reload URI
     * @return reload
     */
    public Reload getZendesk4ReloadByUri(final String reloadUri) {
        notNull(reloadUri, "reloadUri");
        try {
            return webClient.get()
                    .uri(reloadUri)
                    .retrieve()
                    .bodyToMono(Reload.class)
                    .block();
        } catch (Exception e) {
            throw new ConnectorException("Unable to get reload", e);
        }
    }


        /**
         * Schedule a new Zendesk4 reload using WebClient.
         * @param project project to reload
         * @param reload reload parameters
         * @return created reload
         */
        public Reload scheduleZendesk4Reload(final Project project, final Reload reload) {
            notNull(project, "project");
            notNull(project.getId(), "project.id");
            notNull(reload, "reload");

            try {
                String url = Reload.URL.replace("{project}", project.getId()); // если в шаблоне есть {projectId}
                return webClient.post()
                        .uri(url)
                        .bodyValue(reload)
                        .retrieve()
                        .bodyToMono(Reload.class)
                        .block();
            } catch (Exception e) {
                throw new ConnectorException("Unable to schedule Zendesk4 reload for project " + project.getId(), e);
            }
        }


    protected FutureResult<ProcessStatus> createProcessPollResult(final String uri) {
        final Map<String, String> match = STATUS_TEMPLATE.match(uri);
        final String connectorType = match.get("connector");
        final String processId = match.get("process");
        return new PollResult<>(this, new SimplePollHandler<ProcessStatus>(uri, ProcessStatus.class) {
            @Override
            public boolean isFinished(final ClientResponse response) {
                int code = response.statusCode().value();
                if (code == 200) {
                    ProcessStatus process = response.bodyToMono(ProcessStatus.class).block();
                    return process != null && process.isFinished();
                } else if (code == 202) {
                    return false;
                }
                throw new ConnectorException(format("%s process %s returned unknown HTTP code %s", connectorType, processId, code));
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
