package com.gooddata.dataload.processes;

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
class ProcessExecutionTask {

    private static final String POLL_LINK = "poll";
    private static final String DETAIL_LINK = "detail";

    private final Map<String,String> links;

    @JsonCreator
    private ProcessExecutionTask(@JsonProperty("links") Map<String, String> links) {
        this.links = links;
    }

    @Deprecated
    String getPollLink() {
        return getPollUri();
    }

    String getPollUri() {
        return links != null ? links.get(POLL_LINK) : null;
    }

    @Deprecated
    String getDetailLink() {
        return getDetailUri();
    }

    String getDetailUri() {
        return links != null ? links.get(DETAIL_LINK) : null;
    }
}
