/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    private static final String ACCOUNT_ID = "17";
    private static final String ID = "11";
    private static final String URI = "/gdc/projects/11";

    @Mock
    private Project project;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AccountService accountService;
    @Mock
    private Account account;

    private ProjectService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ProjectService(restTemplate, accountService);
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);
    }

    @Test
    public void testGetProjects() throws Exception {
        when(restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, ACCOUNT_ID))
                .thenReturn(new Projects(asList(project)));
        final Collection<Project> result = service.getProjects();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectsWithClientException() throws Exception {
        when(restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, ACCOUNT_ID))
                .thenThrow(new GoodDataException(""));
        service.getProjects();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullUri() throws Exception {
        service.getProjectByUri(null);
    }

    @Test
    public void testGetProjectByUri() throws Exception {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectByUri(URI);
        assertThat(result, is(project));
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void testGetProjectByUriNotFound() throws Exception {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(404, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetProjectByUriServerError() throws Exception {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectByUriClientError() throws Exception {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new RestClientException(""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullId() throws Exception {
        service.getProjectById(null);
    }

    @Test
    public void testGetProjectById() throws Exception {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectById(ID);
        assertThat(result, is(project));
    }

}