/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.util.Validate.notNull;

/**
 * A request to perform diff between current project model and given targetModel.
 * Serialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("diffRequest")
@JsonInclude(JsonInclude.Include.ALWAYS)
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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this);
    }
}
