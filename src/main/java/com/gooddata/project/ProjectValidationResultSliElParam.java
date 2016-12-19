/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonTypeName("sli_el")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultSliElParam extends ProjectValidationResultParam {

    private final List<String> ids;
    private final List<String> vals;

    ProjectValidationResultSliElParam(final List<String> ids, final List<String> vals) {
        this.ids = ids;
        this.vals = vals;
    }

    @JsonCreator
    private static ProjectValidationResultSliElParam create(@JsonProperty("ids") List<String> ids, @JsonProperty("vals") List<String> vals) {
        return new ProjectValidationResultSliElParam(ids, vals);
    }

    /**
     * @return list of IDs. IDs are primary property of this param.
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * @return list of values. Values have only informative character and are connected to IDs.
     *
     * @see #getIds()
     */
    public List<String> getVals() {
        return vals;
    }

    /**
     * Returns map of tuples {@code <ID, value>}. Tuples are constructed from lists of IDs and values
     * according these assumptions:
     * <ul>
     *     <li>Sizes of both lists are equal.</li>
     *     <li>ID and it's corresponding value are on the same index.</li>
     * </ul>
     *
     * @see #getIds()
     * @see #getVals()
     *
     * @return {@code null} when ids are {@code null} or map of tuples
     * @throws IllegalStateException <ul>
     *     <li>when ids are not {@code null} and vals are {@code null}</li>
     *     <li>when sizes of ids and vals lists are not equal</li>
     * </ul>
     */
    public Map<String, String> asMap() {
        if (ids == null) {
            return null;
        }

        if (vals == null) {
            throw new IllegalStateException("Values not defined for IDs.");
        }

        if (ids.size() != vals.size()) {
            throw new IllegalStateException(format("Size of list of IDs (%s) and corresponding list of their values (%s)"
                    + " is not equal.", ids.size(), vals.size()));
        }

        final Map<String, String> sliElParamMap = new LinkedHashMap<>(ids.size());
        for (int index = 0; index < ids.size(); index++) {
            sliElParamMap.put(ids.get(index), vals.get(index));
        }
        return sliElParamMap;
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

    @Override
    public int hashCode() {
        int result = ids != null ? ids.hashCode() : 0;
        result = 31 * result + (vals != null ? vals.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
