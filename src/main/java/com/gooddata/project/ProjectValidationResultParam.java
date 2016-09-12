/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents validation result message param, must be of certain type.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "common", value = ProjectValidationResultStringParam.class),
        @JsonSubTypes.Type(name = "object", value = ProjectValidationResultObjectParam.class),
        @JsonSubTypes.Type(name = "sli_el", value = ProjectValidationResultSliElParam.class),
        @JsonSubTypes.Type(name = "gdctime_el", value = ProjectValidationResultGdcTimeElParam.class),
})
public abstract class ProjectValidationResultParam {
}
