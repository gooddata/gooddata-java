/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.AbstractService;
import com.gooddata.account.AccountService;
import org.springframework.web.client.RestTemplate;

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
}
