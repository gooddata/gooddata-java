package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.util.List;

@JsonTypeName("sli_el")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultSliElParam extends ProjectValidationResultElParam {

    ProjectValidationResultSliElParam(final List<String> ids, final List<String> vals) {
        super(ids, vals);
    }

    @JsonCreator
    private static ProjectValidationResultSliElParam create(@JsonProperty("ids") List<String> ids, @JsonProperty("vals") List<String> vals) {
        return new ProjectValidationResultSliElParam(ids, vals);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectValidationResultSliElParam)) return false;

        ProjectValidationResultSliElParam that = (ProjectValidationResultSliElParam) o;

        if (getIds() != null ? !getIds().equals(that.getIds()) : that.getIds() != null) return false;
        if (getVals() != null ? !getVals().equals(that.getVals()) : that.getVals() != null) return false;

        return true;
    }

}
