package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
