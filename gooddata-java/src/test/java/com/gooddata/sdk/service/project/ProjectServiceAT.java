/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.collections.PageRequest;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.project.*;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.IsIterableContaining;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.gooddata.sdk.model.project.ProjectEnvironment.TESTING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Project acceptance tests.
 */
public class ProjectServiceAT extends AbstractGoodDataAT {

    private static final String LOGIN = "john.smith." + UUID.randomUUID() + "@gooddata.com";

    private User user;
    private Account account;

    private static final String DISABLED_STATUS = "DISABLED";

    public ProjectServiceAT() {
        projectToken = getProperty("projectToken");
    }

    @BeforeClass(groups = "isolated_domain")
    public void initIsolatedDomainGroup() {
        account = gd.getAccountService().createAccount(new Account(LOGIN, "nnPvcGXU7f", "FirstName", "LastName"), getProperty("domain"));
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
        assertThat(projects, IsIterableContaining.hasItem(ProjectIdMatcher.hasSameIdAs(project)));
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
        assertThat(project, ProjectIdMatcher.hasSameIdAs(this.project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectByUri() {
        final Project project = gd.getProjectService().getProjectByUri(this.project.getUri());
        assertThat(project, ProjectIdMatcher.hasSameIdAs(this.project));
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
    public void addUserToProject() {
        final Set<Role> roles = gd.getProjectService().getRoles(project);
        final Role role = roles.iterator().next();

        this.user = gd.getProjectService().addUserToProject(project, account, role);

        assertThat(this.user, is(notNullValue()));
    }

    @Test(groups = {"project", "isolated_domain"}, dependsOnMethods = "addUserToProject")
    public void disableUserInProject() {
        user.setStatus(DISABLED_STATUS);

        gd.getProjectService().updateUserInProject(project, user);

        final User user = gd.getProjectService().getUser(project, account);

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
            if (account != null) {
                gd.getAccountService().removeAccount(account);
            }
        } catch (Exception ignored) {}
    }

}
