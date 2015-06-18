/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.PollResult;
import com.gooddata.SimplePollHandler;
import com.gooddata.account.AccountService;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import com.gooddata.featureflag.FeatureFlagService;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.gdc.FeatureFlag;
import com.gooddata.gdc.FeatureFlags;
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
import java.util.List;
import java.util.Set;

import static com.gooddata.gdc.FeatureFlags.AGGREGATED_FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.project.ProjectFeatureFlag.FEATURE_FLAG_TEMPLATE;
import static com.gooddata.project.ProjectFeatureFlags.FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static java.util.Arrays.asList;
import static org.springframework.web.util.UriComponentsBuilder.fromUri;

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

        return new PollResult<>(this, new SimplePollHandler<Project>(uri.getUri(), Project.class) {

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
        return new PollResult<>(this,
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
     * Get first page of paged list of users by given project.
     *
     * @param project project of users
     * @return list of found users or empty list
     */
    public List<User> listUsers(Project project) {
        notNull(project, "project");
        return listUsers(getUsersUri(project));
    }

    /**
     * Get defined page of paged list of users by given project.
     *
     * @param project project of users
     * @param page    page to be retrieved
     * @return list of found users or empty list
     */
    public List<User> listUsers(Project project, Page page) {
        notNull(project, "project");
        notNull(page, "page");
        return listUsers(page.getPageUri(fromUri(getUsersUri(project))));
    }

    private PageableList<User> listUsers(URI uri) {
        try {
            final Users users = restTemplate.getForObject(uri, Users.class);
            if (users == null) {
                return new PageableList<>();
            }
            return users;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list users", e);
        }
    }

    private static URI getUsersUri(Project project) {
        return Users.TEMPLATE.expand(project.getId());
    }

    /**
     * Get set of user role by given project.
     *
     * Note: This makes n+1 API calls to retrieve all role details.
     *
     * @param project project of roles
     * @return set of found roles or empty set
     */
    public Set<Role> getRoles(final Project project) {
        notNull(project, "project");
        final Roles roles = restTemplate.getForObject(Roles.URI, Roles.class, project.getId());
        final Set<Role> result = new HashSet<>();
        for (String roleUri : roles.getRoles()) {
            final Role role = restTemplate.getForObject(roleUri, Role.class);
            role.setUri(roleUri);
            result.add(role);
        }
        return result;
    }

    /**
     * Get role by given URI.
     *
     * @param uri role uri
     * @return found role
     * @throws RoleNotFoundException when the role doesn't exist
     */
    public Role getRoleByUri(String uri) {
        notEmpty(uri, "uri");
        try {
            return restTemplate.getForObject(uri, Role.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new RoleNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get role " + uri, e);
        }
    }

    /**
     * Lists aggregated feature flags for given project and current user (aggregates global, project group, project and user feature flags).
     * It doesn't matter whether feature flag is enabled or not, it'll be included in all cases.
     *
     * @param project project, cannot be null
     * @return list of aggregated feature flags for given project and current user
     * @deprecated use {@link FeatureFlagService#listFeatureFlags(Project)} instead
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<FeatureFlag> listAggregatedFeatureFlags(final Project project) {
        notNull(project, "project");
        try {
            final FeatureFlags featureFlags =
                    restTemplate.getForObject(AGGREGATED_FEATURE_FLAGS_TEMPLATE.expand(project.getId()),
                            FeatureFlags.class);

            if (featureFlags == null) {
                throw new GoodDataException("empty response from API call");
            }

            return featureFlags.getFeatureFlags();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list aggregated feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Lists all project feature flags (only project scoped flags, use {@link FeatureFlagService#listFeatureFlags(Project)}
     * for aggregated flags from all scopes).
     * It doesn't matter whether feature flag is enabled or not, it'll be included in all cases.
     *
     * @param project project, cannot be null
     * @return list of all feature flags for given project
     */
    public List<ProjectFeatureFlag> listFeatureFlags(Project project) {
        notNull(project, "project");
        try {
            final ProjectFeatureFlags projectFeatureFlags =
                    restTemplate.getForObject(FEATURE_FLAGS_TEMPLATE.expand(project.getId()), ProjectFeatureFlags.class);

            if (projectFeatureFlags == null) {
                throw new GoodDataException("empty response from API call");
            }

            return projectFeatureFlags.getItems();
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list project feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Creates new feature flag for given project.
     *
     * Usually, it doesn't make sense to create feature flag that is disabled because
     * this is the same as having no feature flag at all.
     *
     * @param project project for which the feature flag should be created, cannot be null
     * @param featureFlag feature flag to be created, cannot be null
     * @return created feature flag
     */
    public ProjectFeatureFlag createFeatureFlag(final Project project, final ProjectFeatureFlag featureFlag) {
        notNull(project, "project");
        notNull(featureFlag, "featureFlag");

        final String featureFlagsUri = FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString();

        try {
            final URI featureFlagUri = restTemplate.postForLocation(featureFlagsUri, featureFlag);
            notNull(featureFlagsUri, "URI of new featureFlag");
            return getFeatureFlag(featureFlagUri.toString());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create feature flag: " + featureFlag, e);
        }
    }

    /**
     * Get feature flag by unique name (aka "key").
     *
     * @param project project, cannot be null
     * @param featureFlagName name of feature flag, cannot be empty
     * @return feature flag
     */
    public ProjectFeatureFlag getFeatureFlag(final Project project, final String featureFlagName) {
        notNull(project, "project");
        notEmpty(featureFlagName, "featureFlagName");

        return restTemplate.getForObject(getFeatureFlagUri(project, featureFlagName), ProjectFeatureFlag.class);
    }

    /**
     * Updates existing feature flag.
     * Note that it doesn't make sense to update any other field than {@link ProjectFeatureFlag#enabled}.
     *
     * @param featureFlag updated feature flag
     * @return updated feature flag
     */
    public ProjectFeatureFlag updateFeatureFlag(final ProjectFeatureFlag featureFlag) {
        notNull(featureFlag, "featureFlag");
        notEmpty(featureFlag.getUri(), "featureFlag");

        try {
            restTemplate.put(featureFlag.getUri(), featureFlag);
            return getFeatureFlag(featureFlag.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create feature flag: " + featureFlag, e);
        }
    }

    /**
     * Deletes existing project feature flag.
     *
     * @param featureFlag existing project feature flag with links set properly, cannot be null
     */
    public void deleteFeatureFlag(ProjectFeatureFlag featureFlag) {
        notNull(featureFlag, "featureFlag");
        notEmpty(featureFlag.getUri(), "featureFlag URI");

        try {
            restTemplate.delete(featureFlag.getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete feature flag=" + featureFlag, e);
        }
    }


    private ProjectFeatureFlag getFeatureFlag(String featureFlagUri) {
        return restTemplate.getForObject(featureFlagUri, ProjectFeatureFlag.class);
    }

    private String getFeatureFlagUri(final Project project, final String featureFlagName) {
        return FEATURE_FLAG_TEMPLATE.expand(project.getId(), featureFlagName).toString();
    }
}
