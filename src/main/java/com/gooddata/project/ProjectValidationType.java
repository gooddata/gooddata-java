/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gooddata.util.GoodDataToStringBuilder;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectValidationType that = (ProjectValidationType) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
