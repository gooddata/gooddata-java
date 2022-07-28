/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.gooddata.sdk.model.project.Projects.ROOT_NODE;

/**
 * Collection of GoodData projects being returned from API.
 */
@JsonDeserialize(using = Projects.Deserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Projects extends Page<Project> {

    public static final String URI = "/gdc/projects";
    public static final String LIST_PROJECTS_URI = "/gdc/account/profile/{id}/projects";

    static final String ROOT_NODE = "projects";

    static class Deserializer extends PageDeserializer<Projects, Project> {

        protected Deserializer() {
            super(Project.class);
        }

        @Override
        protected Projects createPage(final List<Project> items, final Paging paging, final Map<String, String> links) {
            return new Projects(items, paging);
        }
    }

    public Projects(List<Project> items, Paging paging) {
        super(items, paging);
    }

    /**
     * @return the list of current page items only
     * @deprecated use {@link Page#getPageItems()} or other methods from {@link Page}.
     * Deprecated since version 3.0.0. Will be removed in one of future versions.
     */
    @Deprecated
    public Collection<Project> getProjects() {
        return getPageItems();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
