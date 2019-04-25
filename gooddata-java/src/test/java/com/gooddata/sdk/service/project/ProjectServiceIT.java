/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.GoodDataException;
import com.gooddata.collections.PageRequest;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.*;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gooddata.util.ResourceUtils.*;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ProjectServiceIT extends AbstractGoodDataIT {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String PROJECT_URI = "/gdc/projects/" + PROJECT_ID;

    private Project loading;
    private Project enabled;
    private Project deleted;

    private Account account;
    private Role role;

    @BeforeClass
    public void setUp() throws Exception {
        loading = readObjectFromResource("/project/project-loading.json", Project.class);
        enabled = readObjectFromResource("/project/project.json", Project.class);
        deleted = readObjectFromResource("/project/project-deleted.json", Project.class);

        account = readObjectFromResource("/account/account.json", Account.class);

        role = mock(Role.class);

        doReturn("/gdc/account/profile/1").when(role).getUri();
    }

    @Test
    public void shouldCreateProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Projects.URI)
                .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(PROJECT_URI)))
                .withStatus(202)
        ;
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_URI)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(loading))
                .withStatus(202)
            .thenRespond()
                .withBody(OBJECT_MAPPER.writeValueAsString(enabled))
                .withStatus(200)
        ;

        final Project project = gd.getProjectService().createProject(new Project("TITLE", "AUTH_TOKEN")).get();
        assertThat(project, is(notNullValue()));
        assertThat(project.getTitle(), is("TITLE"));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailWhenPostFails() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Projects.URI)
            .respond()
                .withStatus(400)
        ;
        gd.getProjectService().createProject(new Project("TITLE", "AUTH_TOKEN")).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailWhenPollFails() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Projects.URI)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(PROJECT_URI)))
                .withStatus(202)
        ;
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_URI)
            .respond()
                .withStatus(400)
        ;

        gd.getProjectService().createProject(new Project("TITLE", "AUTH_TOKEN")).get();
    }


    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailWhenCantCreateProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Projects.URI)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(PROJECT_URI)))
                .withStatus(202)
        ;
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_URI)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(deleted))
                .withStatus(200)
        ;

        gd.getProjectService().createProject(new Project("TITLE", "AUTH_TOKEN")).get();
    }

    @Test
    public void shouldRemoveProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(PROJECT_URI)
                .respond()
                .withStatus(204);

        gd.getProjectService().removeProject(enabled);
    }

    @Test
    public void shouldReturnProjectTemplates() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/" + PROJECT_ID + "/templates")
            .respond()
                .withBody(readFromResource("/project/project-templates.json"));

        final Collection<ProjectTemplate> templates = gd.getProjectService().getProjectTemplates(enabled);
        assertThat(templates, is(notNullValue()));
        assertThat(templates, hasSize(1));
    }

    @Test
    public void shouldReturnAvailableValidations() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/" + PROJECT_ID + "/validate")
                    .respond()
                    .withBody(readFromResource("/project/project-validationAvail.json"));

        final Set<ProjectValidationType> validations = gd.getProjectService().getAvailableProjectValidationTypes(enabled);
        assertThat(validations, notNullValue());
        assertThat(validations, hasSize(7));

    }

    @Test
    public void shouldValidateProject() throws Exception {
        final String validateUri = "/gdc/md/"  + PROJECT_ID + "/validate";
        final String task1Uri = validateUri + "/task/TASK_ID";
        final String task2Uri = validateUri + "/task/TASK_ID2";
        final String resultUri = validateUri + "/result/RESULT_ID";

        onRequest()
                .havingPathEqualTo(validateUri)
            .respond()
                .withBody(readFromResource("/project/project-validationAvail.json"));

        onRequest()
                .havingPathEqualTo(validateUri)
                .havingMethodEqualTo("POST")
                .havingBody(jsonEquals(resource("project/project-validate.json")).when(IGNORING_ARRAY_ORDER))
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(task1Uri)))
                .withStatus(201);

        onRequest()
                .havingPathEqualTo(task1Uri)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(task2Uri)))
                .withHeader("Location", task2Uri)
                .withStatus(303);

        onRequest()
                .havingPathEqualTo(task2Uri)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(task2Uri)))
                .withStatus(202)
            .thenRespond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(resultUri)))
                .withHeader("Location", resultUri)
                .withStatus(303);

        onRequest()
                .havingPathEqualTo(resultUri)
            .respond()
                .withBody(readFromResource("/project/project-validationResults.json"))
                .withStatus(200);

        final ProjectValidationResults validateResult = gd.getProjectService().validateProject(enabled).get();

        assertThat(validateResult, notNullValue());
    }

    @Test
    public void shouldValidateProject2() throws Exception {
        final String validateUri = "/gdc/md/" + PROJECT_ID + "/validate";
        final String task1Uri = validateUri + "/task/TASK_ID";
        final String task2Uri = validateUri + "/task/TASK_ID2";
        final String resultUri = validateUri + "/result/RESULT_ID";

        onRequest()
                .havingPathEqualTo(validateUri)
            .respond()
                .withBody(readFromResource("/project/project-validationAvail.json"));

        onRequest()
                .havingPathEqualTo(validateUri)
                .havingMethodEqualTo("POST")
                .havingBody(jsonEquals(resource("project/project-validate.json")).when(IGNORING_ARRAY_ORDER))
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(task1Uri)))
                .withStatus(201);

        onRequest()
                .havingPathEqualTo(task1Uri)
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(task2Uri)))
                .withHeader("Location", task1Uri)
                .withStatus(303)
            .thenRespond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("RUNNING", task2Uri)))
                .withHeader("Location", task1Uri)
                .withStatus(202)
            .thenRespond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(resultUri)))
                .withHeader("Location", resultUri)
                .withStatus(303)
        ;
        onRequest()
                .havingPathEqualTo(resultUri)
            .respond()
                .withBody(readFromResource("/project/project-validationResults.json"))
                .withStatus(200);

        final ProjectValidationResults validateResult = gd.getProjectService().validateProject(enabled).get();

        assertThat(validateResult, notNullValue());
    }

    @Test
    public void shouldReturnProjectRoles() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/roles")
        .respond()
                .withBody(readFromResource("/project/project-roles.json"))
                .withStatus(200);

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/roles/ROLE1")
        .respond()
                .withBody(readFromResource("/project/project-role.json"))
                .withStatus(200);

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/roles/ROLE2")
        .respond()
                .withBody(readFromResource("/project/project-role2.json"))
                .withStatus(200);

        final Set<Role> roles = gd.getProjectService().getRoles(enabled);
        assertThat(roles, notNullValue());
        assertThat(roles, hasSize(2));
    }

    @Test
    public void shouldReturnProjectRoleForUri() {
        final String roleUri = "/gdc/projects/PROJECT_ID/roles/ROLE1";
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(roleUri)
        .respond()
                .withBody(readFromResource("/project/project-role.json"))
                .withStatus(200);

        final Role role = gd.getProjectService().getRoleByUri(roleUri);
        assertThat(role, notNullValue());
        assertThat(role.getTitle(), is("Embedded Dashboard Only"));
    }

    @Test
    public void shouldListPagedUsers() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ProjectService.PROJECT_USERS_TEMPLATE.expand("PROJECT_ID").toString())
        .respond()
                .withBody(readFromResource("/project/project-users.json"))
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ProjectService.PROJECT_USERS_TEMPLATE.expand("PROJECT_ID").toString())
                .havingQueryStringEqualTo("offset=1&limit=1")
        .respond()
                .withBody(readFromResource("/project/project-users-empty.json"))
                .withStatus(200);

        final List<User> firstPage = gd.getProjectService().listUsers(enabled);
        assertThat(firstPage, notNullValue());
        assertThat(firstPage, hasSize(1));

        final List<User> secondPage = gd.getProjectService().listUsers(enabled, new PageRequest(firstPage.size(), 1));
        assertThat(secondPage, notNullValue());
        assertThat(secondPage, empty());
    }

    @Test
    public void shouldSendInvitations() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/invitations")
        .respond()
                .withBody(readFromResource("/project/created-invitations.json"))
                .withStatus(200);

        final CreatedInvitations invitations = gd.getProjectService().sendInvitations(enabled, new Invitation("roman@gooddata.com"));
        assertThat(invitations, is(notNullValue()));
        assertThat(invitations.getInvitationUris(), contains("uri1", "uri2"));
    }

    @Test
    public void addUserToProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(ProjectService.PROJECT_USERS_TEMPLATE.expand("PROJECT_ID").toString())
                .havingBody(jsonEquals(resource("project/addUserRequest.json")).when(IGNORING_ARRAY_ORDER))
        .respond()
                .withBody(readFromResource("/project/projectUsersUpdateResult.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ProjectService.PROJECT_USER_TEMPLATE.expand("PROJECT_ID", "ID").toString())
        .respond()
                .withBody(readFromResource("/project/project-user.json"));

        final User user = gd.getProjectService().addUserToProject(enabled, account, role);

        assertThat(user, notNullValue());
    }

    @Test(expectedExceptions = ProjectUsersUpdateException.class)
    public void addUserToProjectFail() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(ProjectService.PROJECT_USERS_TEMPLATE.expand("PROJECT_ID").toString())
                .havingBody(jsonEquals(resource("project/addUserRequest.json")).when(IGNORING_ARRAY_ORDER))
        .respond()
                .withBody(readFromResource("/project/projectUsersUpdateResultFail.json"));

        gd.getProjectService().addUserToProject(enabled, account, role);
    }

    @Test
    public void disableUserInProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(ProjectService.PROJECT_USERS_TEMPLATE.expand("PROJECT_ID").toString())
                .havingBody(jsonEquals(resource("project/disableUserInProject.json")).when(IGNORING_ARRAY_ORDER))
        .respond()
                .withBody(readFromResource("/project/projectUsersUpdateResult.json"));

        final User user = new User(account, role);
        user.setStatus("DISABLED");

        gd.getProjectService().updateUserInProject(enabled, user);
    }

    @Test
    public void getUserInProject() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ProjectService.PROJECT_USER_TEMPLATE.expand("PROJECT_ID", "ID").toString())
        .respond()
                .withBody(readFromResource("/project/project-user.json"));

        final User user = gd.getProjectService().getUser(enabled, account);

        assertThat(user, notNullValue());
    }

    @Test(expectedExceptions = UserInProjectNotFoundException.class)
    public void getUserInProjectNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(ProjectService.PROJECT_USER_TEMPLATE.expand("PROJECT_ID", "ID").toString())
                .respond()
        .withStatus(404);

        gd.getProjectService().getUser(enabled, account);
    }
}