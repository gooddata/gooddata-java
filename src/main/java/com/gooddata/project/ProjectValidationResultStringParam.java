/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

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
        return GoodDataToStringBuilder.toStringExclude(this);
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
