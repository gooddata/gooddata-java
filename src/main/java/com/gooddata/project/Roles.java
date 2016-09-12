/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Set;

/**
 * List of Role URIs. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("projectRoles")
@JsonIgnoreProperties(ignoreUnknown = true)
class Roles {
    public static final String URI = "/gdc/projects/{projectId}/roles";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final Set<String> roles;

    @JsonCreator
    Roles(@JsonProperty("roles") final Set<String> roles) {
        this.roles = roles;
    }

    Set<String> getRoles() {
        return roles;
    }
}
