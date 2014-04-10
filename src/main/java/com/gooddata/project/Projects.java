/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collection;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Projects {
    private final Collection<Project> projects;

    @JsonCreator
    public Projects(@JsonProperty("projects") Collection<Project> projects) {
        this.projects = projects;
    }

    public Collection<Project> getProjects() {
        return projects;
    }
}
