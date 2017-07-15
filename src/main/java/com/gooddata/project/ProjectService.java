/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.gooddata.AbstractPollHandler;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.PollResult;
import com.gooddata.SimplePollHandler;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.gdc.UriResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gooddata.util.Validate.noNullElements;
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
     * @return MultiPageList list of found users or empty list
     */
    public PageableList<User> listUsers(final Project project) {
        notNull(project, "project");
        return new MultiPageList<>(page -> listUsers(getUsersUri(project, page)));
    }

    /**
     * Get defined page of paged list of users by given project.
     *
     * @param project   project of users
     * @param startPage page to be retrieved first
     * @return MultiPageList list of found users or empty list
     */
    public PageableList<User> listUsers(final Project project, final Page startPage) {
        notNull(project, "project");
        notNull(startPage, "startPage");
        return new MultiPageList<>(startPage, page -> listUsers(getUsersUri(project, page)));
    }

    private PageableList<User> listUsers(final URI uri) {
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

    private static URI getUsersUri(final Project project) {
        return Users.TEMPLATE.expand(project.getId());
    }

    private static URI getUsersUri(final Project project, final Page page) {
        return page.getPageUri(fromUri(getUsersUri(project)));
    }

    private static URI getUserUri(final Project project, final Account account) {
        return User.TEMPLATE.expand(project.getId(), account.getId());
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
        notEmpty(account.getUri(), "account.uri");
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
        notEmpty(project.getId(), "project.id");

        final User user = new User(account, userRoles);

        doPostProjectUsersUpdate(project, user);

        return getUser(project, account);
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

            if (!projectUsersUpdateResult.getFailed().isEmpty()) {
                throw new ProjectUsersUpdateException("Unable to update users: " + projectUsersUpdateResult.getFailed());
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to update users in project", e);
        }
    }
}
