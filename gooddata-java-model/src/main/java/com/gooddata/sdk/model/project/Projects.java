/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.collections.PageableList;
import com.gooddata.collections.PageableListDeserializer;
import com.gooddata.collections.Paging;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

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
public class Projects extends PageableList<Project> {

    public static final String URI = "/gdc/projects";
    public static final String LIST_PROJECTS_URI = "/gdc/account/profile/{id}/projects";
    public static final UriTemplate LIST_PROJECTS_TEMPLATE = new UriTemplate(LIST_PROJECTS_URI);

    static final String ROOT_NODE = "projects";

    static class Deserializer extends PageableListDeserializer<Projects, Project> {

        protected Deserializer() {
            super(Project.class);
        }

        @Override
        protected Projects createList(final List<Project> items, final Paging paging, final Map<String, String> links) {
            return new Projects(items, paging);
        }
    }

    public Projects(List<Project> items, Paging paging) {
        super(items, paging);
    }

    /**
     * @return the list of current page items only
     * @deprecated use {@link PageableList#getCurrentPageItems()} or other methods from {@link PageableList}.
     * Deprecated since version 3.0.0. Will be removed in one of future versions.
     */
    @Deprecated
    public Collection<Project> getProjects() {
        return getCurrentPageItems();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
