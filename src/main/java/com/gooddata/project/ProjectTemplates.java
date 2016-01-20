/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * Collection of project templates.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
