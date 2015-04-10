/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.account.AccountService;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.gdc.UriResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.gooddata.project.FeatureFlag.FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.project.FeatureFlag.FEATURE_FLAG_TEMPLATE;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;

/**
 * List projects, create a project, ...
 * <p>
 * Usage example:
 * <pre><code>
 *     ProjectService projectService = gd.getProjectService();
 *     Collection&lt;Project&gt; projects = projectService.getProjects();
 *     Project project = projectService.createProject(new Project("my project", "MyToken"));
 * </code></pre>
 */
public class ProjectService extends AbstractService {

    private final AccountService accountService;

    /**
     * Constructs service for GoodData project management (list projects, create a project, ...).
     *
     * @param restTemplate   RESTful HTTP Spring template
     * @param accountService GoodData account service
     */
    public ProjectService(RestTemplate restTemplate, AccountService accountService) {
        super(restTemplate);
        this.accountService = notNull(accountService, "accountService");
    }

    /**
     * Get all projects current user has access to.
     *
     * @return collection of all projects current user has access to
     * @throws com.gooddata.GoodDataException when projects can't be accessed
     */
    public Collection<Project> getProjects() {
        try {
            final String id = accountService.getCurrent().getId();
            final Projects projects = restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, id);
            return projects.getProjects();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get projects", e);
        }
    }

    /**
     * Create new project.
     *
     * @param project project to be created
     * @return created project (including very useful id)
     * @throws com.gooddata.GoodDataException when projects creation fails
     */
    public FutureResult<Project> createProject(Project project) {
        notNull(project, "project");

        final UriResponse uri;
        try {
            uri = restTemplate.postForObject(Projects.URI, project, UriResponse.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create project", e);
        }

        if (uri == null) {
            throw new GoodDataException("Empty response when project POSTed to API");
        }

        return new FutureResult<>(this, new SimplePollHandler<Project>(uri.getUri(), Project.class) {

            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                final Project project = extractData(response, Project.class);
                return !project.isPreparing();
            }

            @Override
            protected void onFinish() {
                if (!getResult().isEnabled()) {
                    throw new GoodDataException("Created project " + uri + " is not enabled");
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException("Creating project " + uri + " failed", e);
            }
        });
    }

    /**
     * Get project by URI.
     *
     * @param uri URI of project resource (/gdc/projects/{id})
     * @return project
     * @throws com.gooddata.GoodDataException when project can't be accessed
     */
    public Project getProjectByUri(final String uri) {
        notEmpty(uri, "uri");
        try {
            return restTemplate.getForObject(uri, Project.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ProjectNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get project " + uri, e);
        }
    }

    /**
     * Get project by id.
     *
     * @param id id of project
     * @return project
     * @throws com.gooddata.GoodDataException when project can't be accessed
     */
    public Project getProjectById(String id) {
        notEmpty(id, "id");
        return getProjectByUri(Project.TEMPLATE.expand(id).toString());
    }

    /**
     * Removes given project
     * @param project project to be removed
     * @throws com.gooddata.GoodDataException when project can't be deleted
     */
    public void removeProject(final Project project) {
        notNull(project, "project");
        try {
            restTemplate.delete(project.getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete project " + project.getId(), e);
        }
    }

    public Collection<ProjectTemplate> getProjectTemplates(Project project) {
        notNull(project, "project");
        try {
            final ProjectTemplates templates = restTemplate.getForObject(ProjectTemplate.URI, ProjectTemplates.class, project.getId());
            return templates != null && templates.getTemplatesInfo() != null ? templates.getTemplatesInfo() : Collections.<ProjectTemplate>emptyList();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get project templates", e);
        }
    }

    /**
     * Get available validation types for project. Which can be passed to {@link #validateProject(Project, ProjectValidationType...)}.
     *
     * @param project project to fetch validation types for
     * @return available validations
     */
    public Set<ProjectValidationType> getAvailableProjectValidationTypes(Project project) {
        notNull(project, "project");
        try {
            return restTemplate.getForObject(ProjectValidations.URI, ProjectValidations.class, project.getId()).getValidations();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get project available validation types", e);
        }
    }

    /**
     * Validate project using all available validations.
     *
     * @param project project to validate
     * @return results of validation
     */
    public FutureResult<ProjectValidationResults> validateProject(final Project project) {
        return validateProject(project, getAvailableProjectValidationTypes(project));
    }

    /**
     * Validate project with given validations
     *
     * @param project project to validate
     * @param validations validations to use
     * @return results of validation
     */
    public FutureResult<ProjectValidationResults> validateProject(final Project project, ProjectValidationType... validations) {
        return validateProject(project, new HashSet<>(asList(validations)));
    }

    /**
     * Validate project with given validations
     *
     * @param project project to validate
     * @param validations validations to use
     * @return results of validation
     */
    public FutureResult<ProjectValidationResults> validateProject(final Project project, Set<ProjectValidationType> validations) {
        notNull(project, "project");
        final AsyncTask task;
        try {
            task = restTemplate.postForObject(ProjectValidations.URI, new ProjectValidations(validations), AsyncTask.class, project.getId());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to to start project validation", e);
        }
        return new FutureResult<>(this,
                // PollHandler able to poll on different URIs (by the Location header)
                // poll class is Void because the object returned varies between invocations (even on the same URI)
                new AbstractPollHandler<Void, ProjectValidationResults>(task.getUri(), Void.class, ProjectValidationResults.class) {

                    @Override
                    public boolean isFinished(ClientHttpResponse response) throws IOException {
                        final URI location = response.getHeaders().getLocation();
                        if (location != null) {
                            setPollingUri(location.toString());
                        }
                        final boolean finished = super.isFinished(response);
                        if (finished) {
                            try {
                                final ProjectValidationResults result = restTemplate.getForObject(getPollingUri(), getResultClass());
                                setResult(result);
                            } catch (GoodDataException | RestClientException e) {
                                throw new GoodDataException("Unable to obtain validation results from " + getPollingUri());
                            }
                        }
                        return finished;
                    }

                    @Override
                    public void handlePollResult(final Void pollResult) {
                    }

                    @Override
                    public void handlePollException(final GoodDataRestException e) {
                        throw new GoodDataException("Project validation failed: " + getPollingUri(), e);
                    }
                });
    }


    /**
     * Creates new feature flag for given project - the feature flag is by default enabled.
     * It doesn't make sense to create feature flag that is disabled because this is the same as having no feature
     * flag at all.
     *
     * @param projectId project identifier
     * @param featureFlagName name of feature flag to be enabled
     */
    public FeatureFlag createProjectFeatureFlag(final String projectId, final String featureFlagName) {
        notEmpty(projectId, "projectId cannot be empty.");
        notEmpty(featureFlagName, "featureFlagName cannot be empty!");

        final String featureFlagsUri = FEATURE_FLAGS_TEMPLATE.expand(projectId).toString();

        try {
            final URI featureFlagUri = restTemplate.postForLocation(featureFlagsUri, new FeatureFlag(featureFlagName));
            notNull(featureFlagsUri, "URI of new featureFlag should not be null!");
            return getFeatureFlag(featureFlagUri.toString());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create feature flag: " + featureFlagName, e);
        }
    }

    /**
     * Get feature flag by unique name (aka "key").
     *
     * @param projectId project identifier
     * @param featureFlagName name of feature flag
     * @return feature flag
     */
    public FeatureFlag getProjectFeatureFlag(String projectId, String featureFlagName) {
        return restTemplate.getForObject(getFeatureFlagUri(projectId, featureFlagName), FeatureFlag.class);
    }

    /**
     * Updates existing feature flag using given {@code newValue}.
     *
     * @param projectId project identifier
     * @param featureFlagName name of feature flag to be updated
     * @param newValue new valued for feature flag - true (enabled), false (disabled)
     * @return updated feature flag
     */
    public FeatureFlag updateProjectFeatureFlag(String projectId, String featureFlagName, boolean newValue) {
        notEmpty(projectId, "projectId cannot be empty.");
        notEmpty(featureFlagName, "featureFlagName cannot be empty!");

        final String featureFlagUri = getFeatureFlagUri(projectId, featureFlagName);

        try {
            restTemplate.put(featureFlagUri, new FeatureFlag(featureFlagName, newValue));
            return getFeatureFlag(featureFlagUri);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create feature flag: " + featureFlagName, e);
        }

    }

    private FeatureFlag getFeatureFlag(String featureFlagUri) {
        return restTemplate.getForObject(featureFlagUri, FeatureFlag.class);
    }

    private String getFeatureFlagUri(String projectId, String featureFlagName) {
        return FEATURE_FLAG_TEMPLATE.expand(projectId, featureFlagName).toString();
    }
}
