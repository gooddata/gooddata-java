/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import static com.gooddata.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

/**
 * Process execution task. Deserialization only
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionTask")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessExecutionTask {

    private static final String POLL_LINK = "poll";
    private static final String DETAIL_LINK = "detail";

    private final Map<String,String> links;

    @JsonCreator
    private ProcessExecutionTask(@JsonProperty("links") Map<String, String> links) {
        this.links = links;
    }

    public String getPollUri() {
        return notNullState(links, "links").get(POLL_LINK);
    }

    public String getDetailUri() {
        return notNullState(links, "links").get(DETAIL_LINK);
    }
}
