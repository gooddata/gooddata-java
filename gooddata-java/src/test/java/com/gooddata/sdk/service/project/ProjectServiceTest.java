/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Paging;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.Projects;
import com.gooddata.sdk.model.project.Role;
import com.gooddata.sdk.model.project.User;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.account.AccountService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import static com.gooddata.sdk.service.project.ProjectService.LIST_PROJECTS_TEMPLATE;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    private static final String ACCOUNT_ID = "17";
    private static final String ID = "11";
    private static final String URI = "/gdc/projects/11";
    private static final String ROLE_URI = URI + "/roles/2";

    @Mock
    private Project project;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AccountService accountService;
    @Mock
    private Account account;
    @Mock
    private Role role;

    private ProjectService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();;
        service = new ProjectService(restTemplate, accountService, new GoodDataSettings());
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);
        when(project.getId()).thenReturn(ID);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetProjects() throws Exception {
        doReturn(new Projects(singletonList(project), new Paging(""))).when(restTemplate)
                .getForObject(new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100"), Projects.class);
        final Collection<Project> result = service.getProjects();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectsWithClientException() throws Exception {
        doThrow(new GoodDataException("")).when(restTemplate)
                .getForObject(new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100"), Projects.class);

        service.getProjects();
    }

    @Test
    public void testListProjects() throws Exception {
        doReturn(new Projects(singletonList(project), new Paging(""))).when(restTemplate)
                .getForObject(new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100"), Projects.class);
        final Collection<Project> result = service.listProjects().getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test
    public void testListProjectsWithPage() throws Exception {
        doReturn(new Projects(singletonList(project), new Paging(""))).when(restTemplate)
                .getForObject(new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?offset=2&limit=100"), Projects.class);
        final Collection<Project> result = service.listProjects(new CustomPageRequest(2, 100)).getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListProjectsWithClientException() throws Exception {
        doThrow(new GoodDataException("")).when(restTemplate)
                .getForObject(new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100"), Projects.class);
        service.listProjects();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullUri() {
        service.getProjectByUri(null);
    }

    @Test
    public void testGetProjectByUri() {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectByUri(URI);
        assertThat(result, is(project));
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void testGetProjectByUriNotFound() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(404, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetProjectByUriServerError() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectByUriClientError() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new RestClientException(""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullId() {
        service.getProjectById(null);
    }

    @Test
    public void testGetProjectById() {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectById(ID);
        assertThat(result, is(project));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullProject() {
        service.addUserToProject(null, mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullAccount() {
        service.addUserToProject(mock(Project.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullAccountUri() {
        service.addUserToProject(mock(Project.class), mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateUserInProjectNullProject() {
        service.updateUserInProject(null, mock(User.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateUserInProjectNullUser() {
        service.updateUserInProject(mock(Project.class), (User) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetUserInProjectNullProject() {
        service.getUser(null, mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetUserInProjectNullAccount() {
        service.getUser(mock(Project.class), null);
    }

    @Test
    public void testGetRoleByUri() {
        when(restTemplate.getForObject(ROLE_URI, Role.class)).thenReturn(role);

        final Role roleByUri = service.getRoleByUri(ROLE_URI);

        verify(role).setUri(ROLE_URI);
        assertThat(roleByUri, is(role));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeUserFromProjectInvalidProject(){
        service.removeUserFromProject(project, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void removeUserFromProjectInvalidAccount(){
        service.removeUserFromProject(null, account);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void removeUserProjectFail() throws URISyntaxException {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        doThrow(GoodDataRestException.class).when(restTemplate).delete(new URI("/gdc/projects/1/users/1"));
        service.removeUserFromProject(project, account);
    }

    @Test(expectedExceptions = GoodDataException.class,
          expectedExceptionsMessageRegExp= "You cannot leave the project 1 if you are the " +
              "only admin in it. You can make another user an admin in this project, and then re-issue the call.")
    public void removeUserProjectFailForbidden() throws URISyntaxException {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        final GoodDataRestException goodDataRestException = new GoodDataRestException(
            HttpStatus.FORBIDDEN.value(),
            "r1",
            "not allowd",
            "component",
            "errorClass");
        doThrow(goodDataRestException).when(restTemplate).delete(new URI("/gdc/projects/1/users/1"));
        service.removeUserFromProject(project, account);
    }

    @Test(expectedExceptions = GoodDataException.class,
          expectedExceptionsMessageRegExp= "You either misspelled your user ID or tried to remove another user but " +
              "did not have the canSuspendUser permission in this project. Check your ID in the request and your " +
              "permissions in the project 1, then re-issue the call.")
    public void removeUserProjectFailNotAllowed() throws URISyntaxException {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        final GoodDataRestException goodDataRestException = new GoodDataRestException(
            HttpStatus.METHOD_NOT_ALLOWED.value() ,
            "r1",
            "not allowd",
            "component",
            "errorClass");
        doThrow(goodDataRestException).when(restTemplate).delete(new URI("/gdc/projects/1/users/1"));
        service.removeUserFromProject(project, account);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void removeUserProjectFailRestletClientException() throws URISyntaxException {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        doThrow(RestClientException.class).when(restTemplate).delete(new URI("/gdc/projects/1/users/1"));
        service.removeUserFromProject(project, account);
    }

    @Test
    public void removeUserProject() throws URISyntaxException {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        doNothing().when(restTemplate).delete(new URI("/gdc/projects/1/users/1"));
        service.removeUserFromProject(project, account);
    }

}