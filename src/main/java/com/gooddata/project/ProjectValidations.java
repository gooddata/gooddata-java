package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Possible validations for project.
 * Helper dto, to fetch available validations or start validations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
class ProjectValidations {

    public static final String URI = "/gdc/md/{projectId}/validate";

    private final Set<ProjectValidationType> validations;

    ProjectValidations(ProjectValidationType... validations) {
        this(new HashSet<>(asList(validations)));
    }

    @JsonCreator
    ProjectValidations(@JsonProperty("availableValidations") final Set<ProjectValidationType> validations) {
        this.validations = validations;
    }

    @JsonProperty("validateProject")
    public Set<ProjectValidationType> getValidations() {
        return validations;
    }

}
