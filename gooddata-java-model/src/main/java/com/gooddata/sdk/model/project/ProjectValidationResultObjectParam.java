/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

@JsonTypeName("object")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultObjectParam extends ProjectValidationResultParam {
    private final String name;
    private final String uri;

    ProjectValidationResultObjectParam(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @JsonCreator
    private static ProjectValidationResultObjectParam create(@JsonProperty("name") String name, @JsonProperty("uri") String uri) {
        return new ProjectValidationResultObjectParam(name, uri);
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ProjectValidationResultObjectParam that = (ProjectValidationResultObjectParam) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return uri != null ? uri.equals(that.uri) : that.uri == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }
}
