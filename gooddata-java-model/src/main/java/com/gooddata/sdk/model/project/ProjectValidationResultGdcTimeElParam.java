/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.List;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ProjectValidationResultGdcTimeElParam that = (ProjectValidationResultGdcTimeElParam) o;

        return ids != null ? ids.equals(that.ids) : that.ids == null;
    }

    @Override
    public int hashCode() {
        return ids != null ? ids.hashCode() : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
