/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.collections.PageRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.gooddata.project.ProjectEnvironment.TESTING;
import static com.gooddata.project.ProjectIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

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

        final Collection<Project> projects = projectService.getProjects();
        assertThat(projects, hasItem(hasSameIdAs(project)));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectUsers() {
        final ProjectService projectService = gd.getProjectService();

        final List<User> users = new ArrayList<>();
        List<User> page;
        while (!(page = projectService.listUsers(project, new PageRequest(users.size(), 100))).isEmpty()) {
            users.addAll(page);
        }
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
        final Project project = gd.getProjectService().getProjectById(this.project.getId());
        assertThat(project, hasSameIdAs(this.project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectByUri() {
        final Project project = gd.getProjectService().getProjectByUri(this.project.getUri());
        assertThat(project, hasSameIdAs(this.project));
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
