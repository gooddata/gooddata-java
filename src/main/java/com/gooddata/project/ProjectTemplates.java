/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
