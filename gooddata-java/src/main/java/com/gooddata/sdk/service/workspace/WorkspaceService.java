/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.workspace;

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
import com.gooddata.sdk.model.workspace.*;
import com.gooddata.sdk.service.*;
import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.workspace.WorkspaceNotFoundException;
import com.gooddata.sdk.service.workspace.WorkspaceUsersUpdateException;
import com.gooddata.sdk.service.workspace.RoleNotFoundException;
import com.gooddata.sdk.service.workspace.UserInWorkspaceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.*;
import static java.util.Arrays.asList;

/**
 * Manage workspaces (create, list, ...)
 * <p>
 * Usage example:
 * <pre><code>
 *     WorkspaceService workspaceService = gd.getWorkspaceService();
 *     Iterable&lt;Workspace&gt; workspaces = workspaceService.listWorkspaces().getAllItems();
 *     Workspace workspace = workspaceService.createWorkspace(new Workspace("My Workspace", "MyToken"));
 * </code></pre>
 */
public class WorkspaceService extends AbstractService {

    public static final UriTemplate WORKSPACE_TEMPLATE = new UriTemplate(Workspace.URI);
    public static final UriTemplate WORKSPACE_USERS_TEMPLATE = new UriTemplate(Users.URI);
    public static final UriTemplate WORKSPACE_USER_TEMPLATE = new UriTemplate(User.URI);
    public static final UriTemplate LIST_WORKSPACES_TEMPLATE = new UriTemplate(Workspaces.LIST_WORKSPACES_URI);
    private final AccountService accountService;

    /**
     * Constructs service for GoodData workspace management (list workspaces, create a workspace, ...).
     * @param restTemplate   RESTful HTTP Spring template
     * @param accountService GoodData account service
     * @param settings settings
     */
    public WorkspaceService(final RestTemplate restTemplate, final AccountService accountService,
                            final GoodDataSettings settings) {
        super(restTemplate, settings);
        this.accountService = notNull(accountService, "accountService");
    }

    /**
     * Get browser of workspaces that current user has access to.
     *
     * @return {@link PageBrowser} list of found workspaces or empty list
     */
    public PageBrowser<Workspace> listWorkspaces() {
        final String userId = accountService.getCurrent().getId();
        return new PageBrowser<>(page -> listWorkspaces(getWorkspacesUri(userId, page)));
    }

    /**
     * Get defined page of paged list of workspaces that current user has access to.
     *
     * @param startPage page to be retrieved first
     * @return {@link PageBrowser} list of found workspaces or empty list
     */
    public PageBrowser<Workspace> listWorkspaces(final PageRequest startPage) {
        notNull(startPage, "startPage");
        final String userId = accountService.getCurrent().getId();
        return new PageBrowser<>(startPage, page -> listWorkspaces(getWorkspacesUri(userId, page)));
    }

    /**
     * Get defined page of paged list of workspaces that given user/account has access to.
     *
     * @param account user whose workspaces will be returned
     * @param startPage page to be retrieved
     * @return {@link PageBrowser} list of found workspaces for given user or empty list
     */
    public PageBrowser<Workspace> listWorkspaces(final Account account, final PageRequest startPage) {
        notNull(startPage, "startPage");
        notNull(account, "account");
        notEmpty(account.getId(), "account.uri");
        return new PageBrowser<>(startPage, page -> listWorkspaces(getWorkspacesUri(account.getId(), page)));
    }

    /**
     * Get browser of workspaces that given user/account has access to.
     *
     * @param account user whose workspaces will be returned
     * @return {@link PageBrowser} list of found workspaces for given user or empty list
     */
    public PageBrowser<Workspace> listWorkspaces(final Account account) {
        return listWorkspaces(account, new CustomPageRequest());
    }

    private Page<Workspace> listWorkspaces(final URI uri) {
        try {
            final Workspaces workspaces = restTemplate.getForObject(uri, Workspaces.class);
            if (workspaces == null) {
                return new Page<>();
            }
            return workspaces;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list workspaces", e);
        }
    }

    private static URI getWorkspacesUri(final String userId) {
        notEmpty(userId, "userId");
        return LIST_WORKSPACES_TEMPLATE.expand(userId);
    }

    private static URI getWorkspacesUri(final String userId, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getWorkspacesUri(userId)));
    }

    /**
     * Create new workspace.
     *
     * @param workspace workspace to be created
     * @return created workspace (including very useful id)
     * @throws GoodDataException when workspaces creation fails
     */
    public FutureResult<Workspace> createWorkspace(final Workspace workspace) {
        notNull(workspace, "workspace");

        final UriResponse uri;
        try {
            uri = restTemplate.postForObject(Workspaces.URI, workspace, UriResponse.class);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create workspace", e);
        }

        if (uri == null) {
            throw new GoodDataException("Empty response when workspace POSTed to API");
        }

        return new PollResult<>(this, new SimplePollHandler<Workspace>(uri.getUri(), Workspace.class) {

            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                final Workspace workspace = extractData(response, Workspace.class);
                return !workspace.isPreparing();
            }

            @Override
            protected void onFinish() {
                if (!getResult().isEnabled()) {
                    throw new GoodDataException("Created workspace " + uri + " is not enabled");
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new GoodDataException("Creating workspace " + uri + " failed", e);
            }
        });
    }

    /**
     * Get workspace by URI.
     *
     * @param uri URI of workspace resource (/gdc/workspaces/{id})
     * @return workspace
     * @throws GoodDataException when workspace can't be accessed
     */
    public Workspace getWorkspaceByUri(final String uri) {
        notEmpty(uri, "uri");
        try {
            return restTemplate.getForObject(uri, Workspace.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new WorkspaceNotFoundException(uri, e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get workspace " + uri, e);
        }
    }

    /**
     * Get workspace by id.
     *
     * @param id id of workspace
     * @return workspace
     * @throws GoodDataException when workspace can't be accessed
     */
    public Workspace getWorkspaceById(String id) {
        notEmpty(id, "id");
        return getWorkspaceByUri(WORKSPACE_TEMPLATE.expand(id).toString());
    }

    /**
     * Removes given workspace
     * @param workspace workspace to be removed
     * @throws GoodDataException when workspace can't be deleted
     */
    public void removeWorkspace(final Workspace workspace) {
        notNull(workspace, "workspace");
        notNull(workspace.getUri(), "workspace.uri");

        try {
            restTemplate.delete(workspace.getUri());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to delete workspace " + workspace.getUri(), e);
        }
    }

    public Collection<WorkspaceTemplate> getWorkspaceTemplates(final Workspace workspace) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");

        try {
            final WorkspaceTemplates templates = restTemplate.getForObject(WorkspaceTemplate.URI, WorkspaceTemplates.class, workspace.getId());
            return templates != null && templates.getTemplatesInfo() != null ? templates.getTemplatesInfo() : Collections.emptyList();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get workspace templates", e);
        }
    }

    /**
     * Get available validation types for workspace. Which can be passed to {@link #validateWorkspace(Workspace, WorkspaceValidationType...)}.
     *
     * @param workspace workspace to fetch validation types for
     * @return available validations
     */
    public Set<WorkspaceValidationType> getAvailableWorkspaceValidationTypes(final Workspace workspace) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");

        try {
            final WorkspaceValidations workspaceValidations = restTemplate.getForObject(WorkspaceValidations.URI, WorkspaceValidations.class, workspace.getId());
            return workspaceValidations != null ? workspaceValidations.getValidations() : Collections.emptySet();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get workspace available validation types", e);
        }
    }

    /**
     * Validate workspace using all available validations.
     *
     * @param workspace workspace to validate
     * @return results of validation
     */
    public FutureResult<WorkspaceValidationResults> validateWorkspace(final Workspace workspace) {
        return validateWorkspace(workspace, getAvailableWorkspaceValidationTypes(workspace));
    }

    /**
     * Validate workspace with given validations
     *
     * @param workspace workspace to validate
     * @param validations validations to use
     * @return results of validation
     */
    public FutureResult<WorkspaceValidationResults> validateWorkspace(final Workspace workspace, WorkspaceValidationType... validations) {
        return validateWorkspace(workspace, new HashSet<>(asList(validations)));
    }

    /**
     * Validate workspace with given validations
     *
     * @param workspace workspace to validate
     * @param validations validations to use
     * @return results of validation
     */
    public FutureResult<WorkspaceValidationResults> validateWorkspace(final Workspace workspace, Set<WorkspaceValidationType> validations) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");

        final AsyncTask task;
        try {
            task = restTemplate.postForObject(WorkspaceValidations.URI, new WorkspaceValidations(validations), AsyncTask.class, workspace.getId());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to to start workspace validation", e);
        }
        return new PollResult<>(this,
                // PollHandler able to poll on different URIs (by the Location header)
                // poll class is Void because the object returned varies between invocations (even on the same URI)
                new AbstractPollHandler<Void, WorkspaceValidationResults>(notNullState(task, "workspace validation task").getUri(),
                        Void.class, WorkspaceValidationResults.class) {

                    @Override
                    public boolean isFinished(ClientHttpResponse response) throws IOException {
                        final URI location = response.getHeaders().getLocation();
                        if (location != null) {
                            setPollingUri(location.toString());
                        }
                        final boolean finished = super.isFinished(response);
                        if (finished) {
                            try {
                                final WorkspaceValidationResults result = restTemplate.getForObject(getPollingUri(), getResultClass());
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
                        throw new GoodDataException("Workspace validation failed: " + getPollingUri(), e);
                    }
                });
    }

    /**
     * Get browser of users by given workspace.
     *
     * @param workspace workspace of users
     * @return {@link PageBrowser} list of found users or empty list
     */
    public PageBrowser<User> listUsers(final Workspace workspace) {
        notNull(workspace, "workspace");
        return new PageBrowser<>(page -> listUsers(getUsersUri(workspace, page)));
    }

    /**
     * Get defined page of paged list of users by given workspace.
     *
     * @param workspace   workspace of users
     * @param startPage page to be retrieved first
     * @return {@link PageBrowser} list of found users or empty list
     */
    public PageBrowser<User> listUsers(final Workspace workspace, final PageRequest startPage) {
        notNull(workspace, "workspace");
        notNull(startPage, "startPage");
        return new PageBrowser<>(startPage, page -> listUsers(getUsersUri(workspace, page)));
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

    private static URI getUsersUri(final Workspace workspace) {
        notNull(workspace.getId(), "workspace.id");
        return WORKSPACE_USERS_TEMPLATE.expand(workspace.getId());
    }

    private static URI getUsersUri(final Workspace workspace, final PageRequest page) {
        return page.getPageUri(new SpringMutableUri(getUsersUri(workspace)));
    }

    private static URI getUserUri(final Workspace workspace, final Account account) {
        notNull(account.getId(), "account.id");
        return WORKSPACE_USER_TEMPLATE.expand(workspace.getId(), account.getId());
    }

    /**
     * Get set of user role by given workspace.
     *
     * Note: This makes n+1 API calls to retrieve all role details.
     *
     * @param workspace workspace of roles
     * @return set of found roles or empty set
     */
    public Set<Role> getRoles(final Workspace workspace) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");

        final Roles roles = restTemplate.getForObject(Roles.URI, Roles.class, workspace.getId());
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
     * Send workspace invitations to users
     * @param workspace target workspace
     * @param invitations invitations
     * @return created invitation
     */
    public CreatedInvitations sendInvitations(final Workspace workspace, final Invitation... invitations) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");
        noNullElements(invitations, "invitations");

        try {
            return restTemplate.postForObject(Invitations.URI, new Invitations(invitations), CreatedInvitations.class, workspace.getId());
        } catch (RestClientException e) {
            final String emails = Arrays.stream(invitations).map(Invitation::getEmail).collect(Collectors.joining(","));
            throw new GoodDataException("Unable to invite " + emails + " to workspace " + workspace.getId(), e);
        }
    }

    /**
     * get user in workspace
     *
     * @param workspace where to find
     * @param account which user to find
     * @return User representation in workspace
     * @throws UserInWorkspaceNotFoundException when user is not in workspace
     */
    public User getUser(final Workspace workspace, final Account account) {
        notNull(account, "account");
        notEmpty(account.getId(), "account.id");
        notNull(workspace, "workspace");

        try {
            return restTemplate.getForObject(getUserUri(workspace, account), User.class);
        } catch (GoodDataRestException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new UserInWorkspaceNotFoundException("User " + account.getId() + " is not in workspace", e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get user " + account.getId() + " in workspace", e);
        }
    }

    /** Add user in to the workspace
     *
     * @param workspace where to add user
     * @param account to be added
     * @param userRoles list of user roles
     * @return user added to the workspace
     * @throws WorkspaceUsersUpdateException in case of failure
     */
    public User addUserToWorkspace(final Workspace workspace, final Account account, final Role... userRoles) {
        notNull(workspace, "workspace");
        notNull(account, "account");
        notEmpty(account.getUri(), "account.uri");
        notNull(userRoles, "userRoles");
        validateRoleURIs(userRoles);
        notEmpty(workspace.getId(), "workspace.id");

        final User user = new User(account, userRoles);

        doPostWorkspaceUsersUpdate(workspace, user);

        return getUser(workspace, account);
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
     * Update user in the workspace
     *
     * @param workspace in which to update user
     * @param users to update
     * @throws WorkspaceUsersUpdateException in case of failure
     */
    public void updateUserInWorkspace(final Workspace workspace, final User... users) {
        notNull(workspace, "workspace");
        notNull(users, "users");
        notEmpty(workspace.getId(), "workspace.id");

        doPostWorkspaceUsersUpdate(workspace, users);
    }

    private void doPostWorkspaceUsersUpdate(final Workspace workspace, final User... users) {
        final URI usersUri = getUsersUri(workspace);

        try {
            final WorkspaceUsersUpdateResult workspaceUsersUpdateResult = restTemplate.postForObject(usersUri, new Users(users), WorkspaceUsersUpdateResult.class);

            if (! notNullState(workspaceUsersUpdateResult, "workspaceUsersUpdateResult").getFailed().isEmpty()) {
                throw new WorkspaceUsersUpdateException("Unable to update users: " + workspaceUsersUpdateResult.getFailed());
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to update users in workspace", e);
        }
    }

    /**
     * Removes given account from a workspace without checking if really account is in workspace.
     * <p>
     *     You can:
     *     <ul>
     *         <li>Remove yourself from a workspace (leave the workspace). You cannot leave the workspace if you are the only admin in the workspace.</li>
     *         <li>Remove another user from a workspace. You need to have the <code>canSuspendUser</code> permission in this workspace.</li>
     *     </ul>
     * </p>
     * @param account account to be removed
     * @param workspace workspace from user will be removed
     * @throws GoodDataException when account can't be removed
     */
    public void removeUserFromWorkspace(final Workspace workspace, final Account account) {
        notNull(workspace, "workspace");
        notNull(workspace.getId(), "workspace.id");
        notNull(account, "account");
        notNull(account.getId(), "account.id");

        try {
            restTemplate.delete(getUserUri(workspace, account));
        } catch (GoodDataRestException e) {
            if (HttpStatus.FORBIDDEN.value() == e.getStatusCode()) {
                throw new GoodDataException("You cannot leave the workspace " + workspace.getId() + " if you are the only admin in it. You can make another user an admin in this workspace, and then re-issue the call.", e);
            } else if (HttpStatus.METHOD_NOT_ALLOWED.value() == e.getStatusCode()) {
                throw new GoodDataException("You either misspelled your user ID or tried to remove another user but did not have the canSuspendUser permission in this workspace. Check your ID in the request and your permissions in the workspace " + workspace.getId() + ", then re-issue the call.", e);
            } else {
                throw e;
            }
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to remove account " + account.getUri() + " from workspace " + workspace.getUri(), e);
        }
    }
}
