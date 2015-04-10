package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
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
