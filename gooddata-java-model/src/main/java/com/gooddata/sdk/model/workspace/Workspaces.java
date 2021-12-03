/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.workspace;

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

import static com.gooddata.sdk.model.workspace.Workspaces.ROOT_NODE;

/**
 * Collection of GoodData workspaces being returned from API.
 */
@JsonDeserialize(using = Workspaces.Deserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Workspaces extends Page<Workspace> {

    public static final String URI = "/gdc/projects";
    public static final String LIST_WORKSPACES_URI = "/gdc/account/profile/{id}/projects";

    static final String ROOT_NODE = "projects";

    static class Deserializer extends PageDeserializer<Workspaces, Workspace> {

        protected Deserializer() {
            super(Workspace.class);
        }

        @Override
        protected Workspaces createPage(final List<Workspace> items, final Paging paging, final Map<String, String> links) {
            return new Workspaces(items, paging);
        }
    }

    public Workspaces(List<Workspace> items, Paging paging) {
        super(items, paging);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
