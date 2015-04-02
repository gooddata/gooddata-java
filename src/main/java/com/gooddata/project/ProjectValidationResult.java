package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectValidationResult that = (ProjectValidationResult) o;

        if (!category.equals(that.category)) return false;
        if (!level.equals(that.level)) return false;
        if (!message.equals(that.message)) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        if (validation != null ? !validation.equals(that.validation) : that.validation != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + level.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (validation != null ? validation.hashCode() : 0);
        return result;
    }

}
