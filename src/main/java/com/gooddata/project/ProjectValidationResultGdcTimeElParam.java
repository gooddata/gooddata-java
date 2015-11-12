package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("gdctime_el")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultGdcTimeElParam extends ProjectValidationResultElParam {

    ProjectValidationResultGdcTimeElParam(final List<String> ids, final List<String> vals) {
        super(ids, vals);
    }

    @JsonCreator
    private static ProjectValidationResultGdcTimeElParam create(@JsonProperty("ids") List<String> ids, @JsonProperty("vals") List<String> vals) {
        return new ProjectValidationResultGdcTimeElParam(ids, vals);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectValidationResultGdcTimeElParam)) return false;

        ProjectValidationResultGdcTimeElParam that = (ProjectValidationResultGdcTimeElParam) o;

        if (getIds() != null ? !getIds().equals(that.getIds()) : that.getIds() != null) return false;
        if (getVals() != null ? !getVals().equals(that.getVals()) : that.getVals() != null) return false;

        return true;
    }

}
