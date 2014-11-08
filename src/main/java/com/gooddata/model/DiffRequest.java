/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static com.gooddata.Validate.notNull;

/**
 * A request to perform diff between current project model and given targetModel.
 * Serialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("diffRequest")
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiffRequest {

    public static final String URI = "/gdc/projects/{project-id}/model/diff";

    @JsonRawValue
    @JsonProperty("targetModel")
    private final String targetModel;

    /**
     * @param targetModel desired target state of project model
     */
    public DiffRequest(String targetModel) {
        this.targetModel = notNull(targetModel, "targetModel");
    }

    /**
     * Returns desired target state of project model
     * @return desired target state of project model
     */
    public String getTargetModel() {
        return targetModel;
    }

}
