/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.common.collections.PageRequest;
import com.gooddata.sdk.common.util.SpringMutableUri;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.CreatedInvitations;
import com.gooddata.sdk.model.project.Invitation;
import com.gooddata.sdk.model.project.Invitations;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.ProjectTemplate;
import com.gooddata.sdk.model.project.ProjectTemplates;
import com.gooddata.sdk.model.project.ProjectUsersUpdateResult;
import com.gooddata.sdk.model.project.ProjectValidationResults;
import com.gooddata.sdk.model.project.ProjectValidationType;
import com.gooddata.sdk.model.project.ProjectValidations;
import com.gooddata.sdk.model.project.Projects;
import com.gooddata.sdk.model.project.Role;
import com.gooddata.sdk.model.project.Roles;
import com.gooddata.sdk.model.project.User;
import com.gooddata.sdk.model.project.Users;
import com.gooddata.sdk.service.AbstractPollHandler;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.FutureResult;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.PollResult;
import com.gooddata.sdk.service.SimplePollHandler;
import com.gooddata.sdk.service.account.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.noNullElements;
import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
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

    public static final UriTemplate PROJECT_TEMPLATE = new UriTemplate(Project.URI);
    public static final UriTemplate PROJECT_USERS_TEMPLATE = new UriTemplate(Users.URI);
    public static final UriTemplate PROJECT_USER_TEMPLATE = new UriTemplate(User.URI);
    public static final UriTemplate LIST_PROJECTS_TEMPLATE = new UriTemplate(Projects.LIST_PROJECTS_URI);
    private final AccountService accountService;

    /**
     * Constructs service for GoodData project management (list projects, create a project, ...).
     * @param restTemplate   RESTful HTTP Spring template
     * @param accountService GoodData account service
     * @param settings settings
     */
    public ProjectService(final RestTemplate restTemplate, final AccountService accountService,
                          final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.accountService = notNull(accountService, "accountService");
    }

    /**
     * Get all projects current user has access to.
     *
     * @return collection of all projects current user has access to
     * @throws com.gooddata.sdk.common.GoodDataException when projects can't be accessed
     * @deprecated use {@link #listProjects()} or {@link #listProjects(PageRequest)} instead.
     * Deprecated since version 3.0.0. Will be removed in one of future versions.
     */
    @Deprecated
    public Collection<Project> getProjects() {
        return listProjects().allItemsStream().collect(Collectors.toList());
    }

    /**
     * Get browser of projects that current user has access to.
     *
     * @return {@link PageBrowser} list of found projects or empty list
     */
    public PageBrowser<Project> listProjects() {
        final String userId = accountService.getCurrent().getId();
        return new PageBrowser<>(page -> listProjects(getProjectsUri(userId, page)));
    }

    /**
     * Get defined page of paged list of projects that current user has access to.
     *
     * @param startPage page to be retrieved first
     * @return {@link PageBrowser} list of found projects or empty list
     */
    public PageBrowser<Project> listProjects(final PageRequest startPage) {
        notNull(startPage, "startPage");
        final String userId = accountService.getCurrent().getId();
        return new PageBrowser<>(startPage, page -> listProjects(getProjectsUri(userId, page)));
    }

    private Page<Project> listProjects(final URI uri) {
        try {
            final Projects projects = restTemplate.getForObject(uri, Projects.class);
            if (projects == null) {
                return new Page<>();
            }
            return projects;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list projects", e);
        }
    }

    private static URI getProjectsUri(final String userId) {
        notEmpty(userId, "userId");
        return LIST_PROJECTS_TEMPLATE.expand(userId);
    }

    private static URI getProjectsUri(final String userId, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getProjectsUri(userId)));
    }

    /**
     * Create new project.
     *
     * @param project project to be created
     * @return created project (including very useful id)
     * @throws com.gooddata.sdk.common.GoodDataException when projects creation fails
     */
    public FutureResult<Project> createProject(final Project project) {
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
     * @throws com.gooddata.sdk.common.GoodDataException when project can't be accessed
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
     * @throws com.gooddata.sdk.common.GoodDataException when project can't be accessed
     */
    public Project getProjectById(String id) {
        notEmpty(id, "id");
        return getProjectByUri(PROJECT_TEMPLATE.expand(id).toString());
    }

    /**
     * Removes given project
     * @param project project to be removed
     * @throws com.gooddata.sdk.common.GoodDataException when project can't be deleted
     */
    public void removeProject(final Project project) {
        notNull(project, "project");
        notNull(project.getUri(), "project.uri");

        try {
            restTemplate.delete(project.getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete project " + project.getUri(), e);
        }
    }

    public Collection<ProjectTemplate> getProjectTemplates(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            final ProjectTemplates templates = restTemplate.getForObject(ProjectTemplate.URI, ProjectTemplates.class, project.getId());
            return templates != null && templates.getTemplatesInfo() != null ? templates.getTemplatesInfo() : Collections.emptyList();
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
    public Set<ProjectValidationType> getAvailableProjectValidationTypes(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            final ProjectValidations projectValidations = restTemplate.getForObject(ProjectValidations.URI, ProjectValidations.class, project.getId());
            return projectValidations != null ? projectValidations.getValidations() : Collections.emptySet();
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
        notNull(project.getId(), "project.id");

        final AsyncTask task;
        try {
            task = restTemplate.postForObject(ProjectValidations.URI, new ProjectValidations(validations), AsyncTask.class, project.getId());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to to start project validation", e);
        }
        return new PollResult<>(this,
                // PollHandler able to poll on different URIs (by the Location header)
                // poll class is Void because the object returned varies between invocations (even on the same URI)
                new AbstractPollHandler<Void, ProjectValidationResults>(notNullState(task, "project validation task").getUri(),
                        Void.class, ProjectValidationResults.class) {

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
     * Get browser of users by given project.
     *
     * @param project project of users
     * @return {@link PageBrowser} list of found users or empty list
     */
    public PageBrowser<User> listUsers(final Project project) {
        notNull(project, "project");
        return new PageBrowser<>(page -> listUsers(getUsersUri(project, page)));
    }

    /**
     * Get defined page of paged list of users by given project.
     *
     * @param project   project of users
     * @param startPage page to be retrieved first
     * @return {@link PageBrowser} list of found users or empty list
     */
    public PageBrowser<User> listUsers(final Project project, final PageRequest startPage) {
        notNull(project, "project");
        notNull(startPage, "startPage");
        return new PageBrowser<>(startPage, page -> listUsers(getUsersUri(project, page)));
    }

    private Page<User> listUsers(final URI uri) {
        try {
            final Users users = restTemplate.getForObject(uri, Users.class);
            if (users == null) {
                return new Page<>();
            }
            return users;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list users", e);
        }
    }

    private static URI getUsersUri(final Project project) {
        notNull(project.getId(), "project.id");
        return PROJECT_USERS_TEMPLATE.expand(project.getId());
    }

    private static URI getUsersUri(final Project project, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getUsersUri(project)));
    }

    private static URI getUserUri(final Project project, final Account account) {
        notNull(account.getId(), "account.id");
        return PROJECT_USER_TEMPLATE.expand(project.getId(), account.getId());
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
        notNull(project.getId(), "project.id");

        final Roles roles = restTemplate.getForObject(Roles.URI, Roles.class, project.getId());
        if (roles == null) {
            return Collections.emptySet();
        } else {
            final Set<Role> result = new HashSet<>();
            for (String roleUri : roles.getRoles()) {
                final Role role = restTemplate.getForObject(roleUri, Role.class);
                notNullState(role, "role").setUri(roleUri);
                result.add(role);
            }
            return result;
        }
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
            final Role role = restTemplate.getForObject(uri, Role.class);
            notNullState(role, "role").setUri(uri);
            return role;
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
     * Send project invitations to users
     * @param project target project
     * @param invitations invitations
     * @return created invitation
     */
    public CreatedInvitations sendInvitations(final Project project, final Invitation... invitations) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        noNullElements(invitations, "invitations");

        try {
            return restTemplate.postForObject(Invitations.URI, new Invitations(invitations), CreatedInvitations.class, project.getId());
        } catch (RestClientException e) {
            final String emails = Arrays.stream(invitations).map(Invitation::getEmail).collect(Collectors.joining(","));
            throw new GoodDataException("Unable to invite " + emails + " to project " + project.getId(), e);
        }
    }

    /**
     * get user in project
     *
     * @param project where to find
     * @param account which user to find
     * @return User representation in project
     * @throws UserInProjectNotFoundException when user is not in project
     */
    public User getUser(final Project project, final Account account) {
        notNull(account, "account");
        notEmpty(account.getId(), "account.id");
        notNull(project, "project");

        try {
            return restTemplate.getForObject(getUserUri(project, account), User.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new UserInProjectNotFoundException("User " + account.getId() + " is not in project", e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get user " + account.getId() + " in project", e);
        }
    }

    /** Add user in to the project
     *
     * @param project where to add user
     * @param account to be added
     * @param userRoles list of user roles
     * @return user added to the project
     * @throws ProjectUsersUpdateException in case of failure
     */
    public User addUserToProject(final Project project, final Account account, final Role... userRoles) {
        notNull(project, "project");
        notNull(account, "account");
        notEmpty(account.getUri(), "account.uri");
        notNull(userRoles, "userRoles");
        validateRoleURIs(userRoles);
        notEmpty(project.getId(), "project.id");

        final User user = new User(account, userRoles);

        doPostProjectUsersUpdate(project, user);

        return getUser(project, account);
    }

    /**
     * Checks whether all the roles have URI specified.
     */
    private void validateRoleURIs(final Role[] userRoles) {
        final List<String> invalidRoles = new ArrayList<>();
        for(Role role: userRoles) {
            if(role.getUri() == null) {
                invalidRoles.add(role.getIdentifier());
            }
        }
        if (!invalidRoles.isEmpty()) {
            throw new IllegalArgumentException("Roles with URI not specified found: " + invalidRoles);
        }
    }

    /**
     * Update user in the project
     *
     * @param project in which to update user
     * @param users to update
     * @throws ProjectUsersUpdateException in case of failure
     */
    public void updateUserInProject(final Project project, final User... users) {
        notNull(project, "project");
        notNull(users, "users");
        notEmpty(project.getId(), "project.id");

        doPostProjectUsersUpdate(project, users);
    }

    private void doPostProjectUsersUpdate(final Project project, final User... users) {
        final URI usersUri = getUsersUri(project);

        try {
            final ProjectUsersUpdateResult projectUsersUpdateResult = restTemplate.postForObject(usersUri, new Users(users), ProjectUsersUpdateResult.class);

            if (! notNullState(projectUsersUpdateResult, "projectUsersUpdateResult").getFailed().isEmpty()) {
                throw new ProjectUsersUpdateException("Unable to update users: " + projectUsersUpdateResult.getFailed());
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to update users in project", e);
        }
    }
}
