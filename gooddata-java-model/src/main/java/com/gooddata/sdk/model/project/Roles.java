/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Set;

/**
 * List of Role URIs. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("projectRoles")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Roles {
    public static final String URI = "/gdc/projects/{projectId}/roles";

    private final Set<String> roles;

    @JsonCreator
    Roles(@JsonProperty("roles") final Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
