/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.project.CreatedInvitations;
import com.gooddata.sdk.model.project.Invitation;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.ProjectValidationResults;
import com.gooddata.sdk.model.project.Role;
import com.gooddata.sdk.model.project.User;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.service.account.AccountService;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.IsIterableContaining;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.gooddata.sdk.model.project.ProjectEnvironment.TESTING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Project acceptance tests.
 */
public class ProjectServiceAT extends AbstractGoodDataAT {

    private static final String LOGIN1 = "john.smith." + UUID.randomUUID() + "@gooddata.com";
    private static final String LOGIN2 = "john.doe." + UUID.randomUUID() + "@gooddata.com";

    private User user;
    private Account account1;
    private Account account2;

    private static final String DISABLED_STATUS = "DISABLED";

    public ProjectServiceAT() {
        projectToken = getProperty("projectToken");
    }

    @BeforeClass(groups = "isolated_domain")
    public void initIsolatedDomainGroup() {
        final AccountService accountService = gd.getAccountService();
        final String domain = getProperty("domain");
        account1 = accountService.createAccount(new Account(LOGIN1, "nnPvcGXU7f", "FirstName1", "LastName1"),
                domain);
        account2 = accountService.createAccount(new Account(LOGIN2, "mmPvcGXU8f", "FirstName2", "LastName2"),
                domain);
    }

    @Test(groups = "project", dependsOnGroups = "account")
    public void createProject() {
        final Project p = new Project(title, projectToken);
        p.setEnvironment(TESTING);
        project = gd.getProjectService().createProject(p).get();
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjects() {
        final ProjectService projectService = gd.getProjectService();

        final PageBrowser<Project> projects = projectService.listProjects();
        assertThat(projects.getAllItems(), IsIterableContaining.hasItem(ProjectIdMatcher.hasSameIdAs(project)));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectUsers() {
        final ProjectService projectService = gd.getProjectService();

        final List<User> users = new ArrayList<>();
        Page<User> page;
        do {
            page = projectService.listUsers(project, new CustomPageRequest(users.size(), 100));
            users.addAll(page.getPageItems());
        } while (page.hasNextPage());
        assertThat(users, not(empty()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectRoles() {
        final ProjectService projectService = gd.getProjectService();

        final Set<Role> roles = projectService.getRoles(project);
        assertThat(roles, not(empty()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectById() {
        final Project result = gd.getProjectService().getProjectById(project.getId());
        assertThat(result, ProjectIdMatcher.hasSameIdAs(project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectByUri() {
        final Project result = gd.getProjectService().getProjectByUri(project.getUri());
        assertThat(result, ProjectIdMatcher.hasSameIdAs(project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void validateProject() {
        final ProjectValidationResults results = gd.getProjectService().validateProject(project).get();
        assertThat(results, is(notNullValue()));
        assertThat(results.getResults(), is(notNullValue()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void sendInvitations() {
        final Invitation invitation = new Invitation("qa+" + RandomStringUtils.randomAlphanumeric(10) + "@gooddata.com");
        final CreatedInvitations invitations = gd.getProjectService().sendInvitations(project, invitation);
        assertThat(invitations, is(notNullValue()));
        assertThat(invitations.getInvitationUris(), hasSize(1));
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = "createProject")
    public void addUsersToProject() {
        final ProjectService projectService = gd.getProjectService();
        final Set<Role> roles = projectService.getRoles(project);
        final Role role = roles.iterator().next();
        this.user = projectService.addUserToProject(project, account1, role);

        // to test scenario with role obtained by its URI
        final Role roleByUri = projectService.getRoleByUri(role.getUri());
        final User user2 = projectService.addUserToProject(project, account2, roleByUri);

        assertThat(this.user, is(notNullValue()));
        assertThat(user2, is(notNullValue()));
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = "addUsersToProject")
    public void listProjectsForUser() {
        final ProjectService projectService = gd.getProjectService();

        final PageBrowser<Project> projects = projectService.listProjects(account1);

        assertThat(projects, is(notNullValue()));
        assertThat(projects.getPageItems().get(0).getTitle(), is(title));
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = "addUsersToProject")
    public void listProjectsForUserSettingStartPage() {
        final ProjectService projectService = gd.getProjectService();

        final PageBrowser<Project> projects = projectService.listProjects(account1, new CustomPageRequest(10, 1));

        assertThat(projects, is(notNullValue()));
        assertThat(projects.getPageItems().isEmpty(), is(true));
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = {"listProjectsForUser", "listProjectsForUserSettingStartPage"})
    public void disableUserInProject() {
        user.setStatus(DISABLED_STATUS);

        gd.getProjectService().updateUserInProject(project, user);

        final User user = gd.getProjectService().getUser(project, account1);

        assertThat(user, notNullValue());
        assertThat(user.getStatus(), is("DISABLED"));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getUserInProject() {
        final User user = gd.getProjectService().getUser(project, gd.getAccountService().getCurrent());

        assertThat(user, notNullValue());
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = "disableUserInProject")
    public void removeAccountFromProject() {
        gd.getProjectService().getUser(project, account1); // just verifying it doesn't throw before the removal
        gd.getProjectService().removeUserFromProject(project, account1);

        try {
            gd.getProjectService().getUser(project, account1);
            assertThat("Exception should be thrown.", false);
        } catch (UserInProjectNotFoundException e) {
            //expected
        }
    }

    @AfterClass(groups = "isolated_domain")
    public void tearDownIsolatedDomainGroup() {
        try {
            if (account1 != null) {
                gd.getAccountService().removeAccount(account1);
            }
            if (account2 != null) {
                gd.getAccountService().removeAccount(account2);
            }
        } catch (Exception ignored) {}
    }

}

