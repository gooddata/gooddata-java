package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Validation result body helper dto. Not exposed to user.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ProjectValidationResultItem {

    private final Body body;
    private final ProjectValidationType validation;

    @JsonCreator
    private ProjectValidationResultItem(@JsonProperty("body") Body body, @JsonProperty("from") ProjectValidationType validation) {
        this.body = body;
        this.validation = validation;
    }

    List<ProjectValidationResult> getLogs() {
        return body.logs;
    }

    ProjectValidationType getValidation() {
        return validation;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Body {

        private final List<ProjectValidationResult> logs;

        @JsonCreator
        private Body(@JsonProperty("log") List<ProjectValidationResult> logs) {
            this.logs = logs;
        }
    }
}
