/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.AbstractService;
import com.gooddata.UriResponse;
import com.gooddata.account.AccountService;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 */
public class ProjectService extends AbstractService {

    private final AccountService accountService;

    public ProjectService(RestTemplate restTemplate, AccountService accountService) {
        super(restTemplate);
        this.accountService = accountService;
    }

    public Collection<Project> getProjects() {
        final String id = accountService.getCurrent().getId();
        final Projects projects = restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, id);
        return projects.getProjects();
    }

    public Project createProject(Project inputProject) {

        final UriResponse uri = restTemplate.postForObject(Projects.URI, inputProject, UriResponse.class);

        return poll(URI.create(uri.getUri()),
                new ConditionCallback() {
                    @Override
                    public boolean finished(ClientHttpResponse response) throws IOException {
                        final Project project = new HttpMessageConverterExtractor<>(Project.class,
                                restTemplate.getMessageConverters()).extractData(response);
                        return "ENABLED".equalsIgnoreCase(project.getContent().getState());
                    }
                }, Project.class
        );
    }

    public Project getProjectByUri(final String uri) {
        return restTemplate.getForObject(uri, Project.class);
    }

    public Project getProjectById(String id) {
        return restTemplate.getForObject(Project.PROJECT_URI, Project.class, id);
    }
}
