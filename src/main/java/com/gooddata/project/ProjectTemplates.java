/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Collection;

/**
 * Collection of project templates.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class ProjectTemplates {

    private final Collection<ProjectTemplate> templatesInfo;

    @JsonCreator
    ProjectTemplates(@JsonProperty("templatesInfo") Collection<ProjectTemplate> templatesInfo) {
        this.templatesInfo = templatesInfo;
    }

    public Collection<ProjectTemplate> getTemplatesInfo() {
        return templatesInfo;
    }
}
