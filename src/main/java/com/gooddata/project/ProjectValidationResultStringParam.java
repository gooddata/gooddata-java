package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeName;

@JsonTypeName("common")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultStringParam extends ProjectValidationResultParam {
    private final String value;

    ProjectValidationResultStringParam(String value) {
        this.value = value;
    }

    // TODO is there some BUG in jackson preventing use the contructor as creator?
    @JsonCreator
    private static ProjectValidationResultStringParam create(String value) {
        return new ProjectValidationResultStringParam(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectValidationResultStringParam that = (ProjectValidationResultStringParam) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
