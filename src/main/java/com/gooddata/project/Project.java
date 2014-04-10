/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    public static final String PROJECTS_URI = "/gdc/account/profile/{id}/projects";


    private String id;


    public Project(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
