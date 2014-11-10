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
