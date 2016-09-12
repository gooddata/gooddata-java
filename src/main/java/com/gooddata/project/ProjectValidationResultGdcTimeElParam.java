/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.Objects;

@JsonTypeName("gdctime_el")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultGdcTimeElParam extends ProjectValidationResultParam {

    private final List<String> ids;

    ProjectValidationResultGdcTimeElParam(final List<String> ids) {
        this.ids = ids;
    }

    @JsonCreator
    private static ProjectValidationResultGdcTimeElParam create(@JsonProperty("ids") List<String> ids) {
        return new ProjectValidationResultGdcTimeElParam(ids);
    }

    public List<String> getIds() {
        return ids;
    }

    /**
     * @deprecated for backward compatibility only. Do not use this method, it always returns null.
     */
    @Deprecated
    public List<String> getVals() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ProjectValidationResultGdcTimeElParam))
            return false;
        ProjectValidationResultGdcTimeElParam that = (ProjectValidationResultGdcTimeElParam) o;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }

    @Override
    public String toString() {
        return ids.toString();
    }
}
