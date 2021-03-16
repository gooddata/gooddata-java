/*
 * (C) 2021 GoodData Corporation.
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
 * Represents one validation result.
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResult {

    private final String category;
    private final String level;
    private final String message;
    private final List<ProjectValidationResultParam> params;

    private static final String LEVEL_WARNING = "WARN";
    private static final String LEVEL_ERROR = "ERROR";

    private ProjectValidationType validation;

    @JsonCreator
    private ProjectValidationResult(@JsonProperty("ecat") String category, @JsonProperty("level") String level,
                                    @JsonProperty("msg") String message, @JsonProperty("pars") List<ProjectValidationResultParam> params) {
        this.category = category;
        this.level = level;
        this.message = message;
        this.params = params;
    }

    public String getCategory() {
        return category;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public List<ProjectValidationResultParam> getParams() {
        return params;
    }

    public boolean isError() {
        return LEVEL_ERROR.equals(level);
    }

    public ProjectValidationType getValidation() {
        return validation;
    }

    // package protected by design (used inside ProjectValidationResults deserialization constructor)
    void setValidation(ProjectValidationType validation) {
        this.validation = validation;
    }

    public boolean isWarning() {
        return LEVEL_WARNING.equals(level);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ProjectValidationResult that = (ProjectValidationResult) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        return validation != null ? validation.equals(that.validation) : that.validation == null;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (validation != null ? validation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

}
