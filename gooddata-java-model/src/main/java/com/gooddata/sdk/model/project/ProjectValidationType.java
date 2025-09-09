/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

/**
 * Represents project validation type.
 */
public class ProjectValidationType {

    private final String value;

    public static final ProjectValidationType PDM_VS_DWH = new ProjectValidationType("pdm::pdm_vs_dwh");
    public static final ProjectValidationType METRIC_FILTER = new ProjectValidationType("metric_filter");
    public static final ProjectValidationType PDM_TRANSITIVITY = new ProjectValidationType("pdm::transitivity");
    public static final ProjectValidationType LDM = new ProjectValidationType("ldm");
    public static final ProjectValidationType INVALID_OBJECTS = new ProjectValidationType("invalid_objects");
    public static final ProjectValidationType PDM_ELEM = new ProjectValidationType("pdm::elem_validation");
    public static final ProjectValidationType PDM_PK_FK_CONSISTENCY = new ProjectValidationType("pdm::pk_fk_consistency");

    @JsonCreator
    public ProjectValidationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ProjectValidationType that = (ProjectValidationType) o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

