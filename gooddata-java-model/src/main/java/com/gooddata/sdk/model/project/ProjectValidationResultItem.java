/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Body {

        private final List<ProjectValidationResult> logs;

        @JsonCreator
        private Body(@JsonProperty("log") List<ProjectValidationResult> logs) {
            this.logs = logs;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
