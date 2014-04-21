/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Projects {

    public static final String URI = "/gdc/projects";

    private final Collection<Project> projects;

    @JsonCreator
    public Projects(@JsonProperty("projects") Collection<Project> projects) {
        this.projects = projects;
    }

    public Collection<Project> getProjects() {
        return projects;
    }
}
