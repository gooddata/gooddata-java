/*
 * (C) 2023 GoodData Corporation.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.service.project.ProjectService.LIST_PROJECTS_TEMPLATE;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private static final String ACCOUNT_ID = "17";
    private static final String ID = "11";
    private static final String URI = "/gdc/projects/11";
    private static final String ROLE_URI = URI + "/roles/2";

    @Mock
    private Project project;
    @Mock
    private WebClient webClient;
    @Mock
    private AccountService accountService;
    @Mock
    private Account account;
    @Mock
    private Role role;

    private ProjectService service;


    @BeforeEach
    void setUp() {
        service = new ProjectService(webClient, accountService, new GoodDataSettings());
        lenient().when(accountService.getCurrent()).thenReturn(account);
        lenient().when(account.getId()).thenReturn(ACCOUNT_ID);
        lenient().when(project.getId()).thenReturn(ID);
    }

    @Test
    void testGetProjects() throws Exception {

        Projects projectsResponse = new Projects(
            Collections.singletonList(project),
            new Paging("someNextPageUrl")
        );
        Projects emptyProjects = new Projects(
            Collections.emptyList(),
            new Paging((String) null)
        );

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Projects.class))
            .thenReturn(Mono.just(projectsResponse))
            .thenReturn(Mono.just(emptyProjects));

        Collection<Project> result = service.getProjects();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }


    @Test
    void testGetProjectsWithClientException() throws Exception {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.error(new GoodDataException("")));

        assertThrows(GoodDataException.class, () -> service.getProjects());
    }

    @Test
    void testListProjects() throws Exception {

        Projects projectsResponse = new Projects(Collections.singletonList(project), new Paging(""));

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.just(projectsResponse));

        Collection<Project> result = service.listProjects().getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test
    void testListProjectsWithPage() throws Exception {
        Projects projectsResponse = new Projects(Collections.singletonList(project), new Paging(""));
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?offset=2&limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.just(projectsResponse));


        Collection<Project> result = service.listProjects(new CustomPageRequest(2, 100)).getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test
    void testListProjectsWithClientException() throws Exception {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.error(new GoodDataException("")));

        assertThrows(GoodDataException.class, () -> service.listProjects());
    }

    @Test
    void testListProjectsForUserWithPage() throws Exception {

        Projects projectsResponse = new Projects(Collections.singletonList(project), new Paging(""));

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?offset=1&limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.just(projectsResponse));

        Collection<Project> result = service.listProjects(account, new CustomPageRequest(1, 100)).getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test
    void testListProjectsForUser() throws Exception {

        Projects projectsResponse = new Projects(Collections.singletonList(project), new Paging(""));

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.just(projectsResponse));

        Collection<Project> result = service.listProjects(account).getPageItems();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test
    void testListProjectsForUserWithClientException() throws Exception {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI(LIST_PROJECTS_TEMPLATE.expand(ACCOUNT_ID) + "?limit=100");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Projects.class)).thenReturn(Mono.error(new GoodDataException("")));

        assertThrows(GoodDataException.class, () -> service.listProjects());
    }

    @Test
    void testGetProjectByUriWithNullUri() {
        assertThrows(IllegalArgumentException.class, () -> service.getProjectByUri(null));
    }

    @Test
    void testGetProjectByUri() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.just(project));

        final Project result = service.getProjectByUri(URI);
        assertThat(result, is(project));
    }

    @Test
    void testGetProjectByUriNotFound() {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Project.class))
                .thenReturn(Mono.error(new GoodDataRestException(404, "", "", "", "")));

        assertThrows(ProjectNotFoundException.class, () -> service.getProjectByUri(URI));
    }

    @Test
    void testGetProjectByUriServerError() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Project.class))
                .thenReturn(Mono.error(new GoodDataRestException(500, "", "", "", "")));

        assertThrows(GoodDataRestException.class, () -> service.getProjectByUri(URI));
    }

    @Test
    void testGetProjectByUriClientError() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.error(new RuntimeException("")));

        assertThrows(GoodDataException.class, () -> service.getProjectByUri(URI));
    }

    @Test
    void testGetProjectByUriWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.getProjectById(null));
    }

    @Test
    void testGetProjectById() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);


        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Project.class)).thenReturn(Mono.just(project));

        final Project result = service.getProjectById(ID);
        assertThat(result, is(project));
    }

    @Test
    void testAddUserToProjectNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.addUserToProject(null, mock(Account.class)));
    }

    @Test
    void testAddUserToProjectNullAccount() {
        assertThrows(IllegalArgumentException.class, () -> service.addUserToProject(mock(Project.class), null));
    }

    @Test
    void testAddUserToProjectNullAccountUri() {
        assertThrows(IllegalArgumentException.class, () -> service.addUserToProject(mock(Project.class), mock(Account.class)));
    }

    @Test
    void testUpdateUserInProjectNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.updateUserInProject(null, mock(User.class)));
    }

    @Test
    void testUpdateUserInProjectNullUser() {
        assertThrows(IllegalArgumentException.class, () -> service.updateUserInProject(mock(Project.class), (User) null));
    }

    @Test
    void testGetUserInProjectNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getUser(null, mock(Account.class)));
    }

    @Test
    void testGetUserInProjectNullAccount() {
        assertThrows(IllegalArgumentException.class, () -> service.getUser(mock(Project.class), null));
    }

    @Test
    void testGetRoleByUri() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(ROLE_URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Role.class)).thenReturn(Mono.just(role));

        final Role roleByUri = service.getRoleByUri(ROLE_URI);

        verify(role).setUri(ROLE_URI);
        assertThat(roleByUri, is(role));
    }

    @Test
    void removeUserFromProjectInvalidProject() {
        assertThrows(IllegalArgumentException.class, () -> service.removeUserFromProject(project, null));
    }

    @Test
    void removeUserFromProjectInvalidAccount() {
        assertThrows(IllegalArgumentException.class, () -> service.removeUserFromProject(null, account));
    }

    @Test
    void removeUserProjectFailUnexpectedly() throws Exception {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");

        final GoodDataRestException goodDataRestException = new GoodDataRestException(
                500, "r1", "server error", "component", "errorClass"
        );

        URI userUri = new URI("/gdc/projects/" + project.getId() + "/users/" + account.getId());

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(userUri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(goodDataRestException));

        GoodDataException thrown = assertThrows(
                GoodDataException.class,
                () -> service.removeUserFromProject(project, account)
        );

        assertThat(thrown.getMessage(),
            org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("500"),
                org.hamcrest.Matchers.containsString("r1"),
                org.hamcrest.Matchers.containsString("server error")
            )
        );
    }



    @Test
    void removeUserProjectFailForbidden() throws Exception {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");
        final GoodDataRestException goodDataRestException = new GoodDataRestException(
            403, // HttpStatus.FORBIDDEN.value()
            "r1",

            "You cannot leave the project 1 if you are the only admin in it. " +
            "You can make another user an admin in this project, and then re-issue the call.",
            "component",
            "errorClass"
        );

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI("/gdc/projects/1/users/1");

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(goodDataRestException));

        GoodDataException thrown = assertThrows(
            GoodDataException.class,
            () -> service.removeUserFromProject(project, account)
        );

        assertThat(thrown.getMessage(),
            containsString("You cannot leave the project 1 if you are the only admin in it. " +
                        "You can make another user an admin in this project, and then re-issue the call."));
    }

/* 
    public void removeUserFromProject(final Project project, final Account account) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");
        notNull(account, "account");
        notNull(account.getId(), "account.id");
        try {
            webClient.delete()
                .uri("/gdc/projects/" + project.getId() + "/users/" + account.getId())
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            Throwable t = e;
            while (t != null) {
                if (t instanceof GoodDataRestException ||
                    t.getClass().getSimpleName().equals("GoodDataRestException")) {
                    throw new GoodDataException(t.getMessage(), t);
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
*/

    @Test
    void removeUserProjectFailWithRestClientException() throws Exception {
        when(project.getId()).thenReturn("1");
        when(project.getUri()).thenReturn("/gdc/projects/1");
        when(account.getId()).thenReturn("1");
        when(account.getUri()).thenReturn("/gdc/account/1");

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI("/gdc/projects/1/users/1");

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        lenient().when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException()));

        GoodDataException thrown = assertThrows(
            GoodDataException.class,
            () -> service.removeUserFromProject(project, account)
        );

        assertThat(
            thrown.getMessage(),
            containsString("Unable to remove account /gdc/account/1 from project /gdc/projects/1")
        );
    }

    @Test
    void removeUserProject() throws Exception {
        when(project.getId()).thenReturn("1");
        when(account.getId()).thenReturn("1");

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        URI uri = new URI("/gdc/projects/1/users/1");

        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        service.removeUserFromProject(project, account);

        verify(webClient).delete();
        verify(uriSpec).uri(eq(uri));
        verify(headersSpec).retrieve();
        verify(responseSpec).toBodilessEntity();
    }


}