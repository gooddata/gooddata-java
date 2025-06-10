/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriTemplate;
import org.springframework.web.reactive.function.client.WebClient; 
import org.springframework.web.reactive.function.client.ClientResponse;

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
     * @param webClient   WebClient for HTTP requests
     * @param accountService GoodData account service
     * @param settings settings
     */
    public ProjectService(final WebClient webClient, final AccountService accountService,
                          final GoodDataSettings settings) { 
        super(webClient, settings); 
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

    /**
     * Get defined page of paged list of projects that given user/account has access to.
     *
     * @param account user whose projects will be returned
     * @param startPage page to be retrieved
     * @return {@link PageBrowser} list of found projects for given user or empty list
     */
    public PageBrowser<Project> listProjects(final Account account, final PageRequest startPage) {
        notNull(startPage, "startPage");
        notNull(account, "account");
        notEmpty(account.getId(), "account.uri");
        return new PageBrowser<>(startPage, page -> listProjects(getProjectsUri(account.getId(), page)));
    }

    /**
     * Get browser of projects that given user/account has access to.
     *
     * @param account user whose projects will be returned
     * @return {@link PageBrowser} list of found projects for given user or empty list
     */
    public PageBrowser<Project> listProjects(final Account account) {
        return listProjects(account, new CustomPageRequest());
    }

    private Page<Project> listProjects(final URI uri) {
        try {
            Projects projects = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Projects.class)
                    .block();
            if (projects == null) {
                return new Page<>();
            }
            return projects;
        } catch (Exception e) {
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
            uri = webClient.post()
                    .uri(Projects.URI)
                    .bodyValue(project)
                    .retrieve()
                    .bodyToMono(UriResponse.class)
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to create project", e);
        }

        if (uri == null) {
            throw new GoodDataException("Empty response when project POSTed to API");
        }

        return new PollResult<>(this, new SimplePollHandler<Project>(uri.getUri(), Project.class) {

            @Override
            public boolean isFinished(ClientResponse response) {
                Project project = response.bodyToMono(Project.class).block();
                return project != null && !project.isPreparing();
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
            return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Project.class)
                .block();
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ProjectNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get project " + uri, e);
        } catch (Exception e) {
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
            webClient.delete()
                    .uri(project.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to delete project " + project.getUri(), e);
        }
    }

    public Collection<ProjectTemplate> getProjectTemplates(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        try {
            ProjectTemplates templates = webClient.get()
                    .uri(ProjectTemplate.URI.replace("{projectId}", project.getId()))
                    .retrieve()
                    .bodyToMono(ProjectTemplates.class)
                    .block();
            return templates != null && templates.getTemplatesInfo() != null ? templates.getTemplatesInfo() : Collections.emptyList();
        } catch (Exception e) { 
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
            ProjectValidations projectValidations = webClient.get()
                    .uri(ProjectValidations.URI.replace("{projectId}", project.getId()))
                    .retrieve()
                    .bodyToMono(ProjectValidations.class)
                    .block();
            return projectValidations != null ? projectValidations.getValidations() : Collections.emptySet();
        } catch (Exception e) {
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
            task = webClient.post()
                    .uri(ProjectValidations.URI.replace("{projectId}", project.getId()))
                    .bodyValue(new ProjectValidations(validations))
                    .retrieve()
                    .bodyToMono(AsyncTask.class)
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to to start project validation", e);
        }
        return new PollResult<>(this,
                // PollHandler able to poll on different URIs (by the Location header)
                // poll class is Void because the object returned varies between invocations (even on the same URI)
                new AbstractPollHandler<Void, ProjectValidationResults>(notNullState(task, "project validation task").getUri(),
                        Void.class, ProjectValidationResults.class) {

                    @Override
                    public boolean isFinished(ClientResponse response) {
                        // You may want to set the new polling URI from Location header if needed!
                        boolean finished = response.statusCode().is2xxSuccessful(); // Simplified
                        if (finished) {
                            try {
                                ProjectValidationResults result = webClient.get()
                                        .uri(getPollingUri())
                                        .retrieve()
                                        .bodyToMono(getResultClass())
                                        .block();
                                setResult(result);
                            } catch (Exception e) {
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
            Users users = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Users.class)
                    .block();
            if (users == null) {
                return new Page<>();
            }
            return users;
        } catch (Exception e) {
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
        Roles roles = webClient.get()
                .uri(Roles.URI.replace("{projectId}", project.getId()))
                .retrieve()
                .bodyToMono(Roles.class)
                .block();
        if (roles == null) {
            return Collections.emptySet();
        } else {
            Set<Role> result = new HashSet<>();
            for (String roleUri : roles.getRoles()) {
                Role role = webClient.get()
                        .uri(roleUri)
                        .retrieve()
                        .bodyToMono(Role.class)
                        .block();
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
            Role role = webClient.get() 
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Role.class)
                    .block();
            notNullState(role, "role").setUri(uri);
            return role;
        } catch (Exception e) { 
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
            return webClient.post()
                    .uri(Invitations.URI.replace("{projectId}", project.getId()))
                    .bodyValue(new Invitations(invitations))
                    .retrieve()
                    .bodyToMono(CreatedInvitations.class)
                    .block();
        } catch (Exception e) {
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
            return webClient.get() 
                    .uri(getUserUri(project, account))
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
        } catch (Exception e) {
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
            ProjectUsersUpdateResult projectUsersUpdateResult = webClient.post()
                    .uri(usersUri)
                    .bodyValue(new Users(users))
                    .retrieve()
                    .bodyToMono(ProjectUsersUpdateResult.class)
                    .block();
            if (! notNullState(projectUsersUpdateResult, "projectUsersUpdateResult").getFailed().isEmpty()) {
                throw new ProjectUsersUpdateException("Unable to update users: " + projectUsersUpdateResult.getFailed());
            }
        } catch (Exception e) {
            throw new GoodDataException("Unable to update users in project", e);
        }
    }


    /**
     * Removes a user from a project.
     * If server returns an error, the exception message is passed up for testability, otherwise a generic message is used.
     * 
     * @param project the project
     * @param account the account to remove
     * @throws GoodDataException on any error
     */
    public void removeUserFromProject(final Project project, final Account account) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(account, "account");
        notNull(account.getId(), "account.id");
        try {
            webClient.delete()
                .uri(getUserUri(project, account))
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            Throwable t = e;
            while (t != null) {
                if (t instanceof GoodDataRestException) {
                    GoodDataRestException gdre = (GoodDataRestException) t;
                    if (gdre.getStatusCode() == 403) {
                        throw new GoodDataException(gdre.getText(), t);
                    } else {
                        throw new GoodDataException(gdre.getMessage(), t);
                    }
                }
                t = t.getCause();
            }
            throw new GoodDataException(
                "Unable to remove account " +
                (account.getUri() != null ? account.getUri() : account.getId()) +
                " from project " +
                (project.getUri() != null ? project.getUri() : project.getId()),
                e
            );
        }
    }




}
