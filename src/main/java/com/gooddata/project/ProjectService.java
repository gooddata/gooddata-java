/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.AbstractService;
import com.gooddata.UriResponse;
import com.gooddata.account.AccountService;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

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
                new ConditionCallback<Project>() {
                    @Override
                    public boolean finished(HttpEntity<Project> entity) {
                        return "ENABLED".equalsIgnoreCase(entity.getBody().getContent().getState());
                    }
                }, Project.class
        );
    }

    public Project getProjectByUri(final String uri) {
        return restTemplate.getForObject(uri, Project.class);
    }
}
