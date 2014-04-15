/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.annotate.JsonRawValue;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static org.apache.commons.lang.Validate.notNull;


/**
 * A request to perform diff between current project model and given targetModel.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("diffRequest")
@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiffRequest {

    public static final String URI = "/gdc/projects/{project-id}/model/diff";

    @JsonRawValue
    private final String targetModel;

    /**
     * @param targetModel desired target state of project model
     */
    @JsonCreator
    public DiffRequest(@JsonProperty("targetModel") String targetModel) {
        notNull(targetModel, "targetModel cannot be null");
        this.targetModel = targetModel;
    }

    /**
     * Returns desired target state of project model
     */
    public String getTargetModel() {
        return targetModel;
    }

}
