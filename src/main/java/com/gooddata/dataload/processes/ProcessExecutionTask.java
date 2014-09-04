package com.gooddata.dataload.processes;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

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

    String getPollLink() {
        return links != null ? links.get(POLL_LINK) : null;
    }

    String getDetailLink() {
        return links != null ? links.get(DETAIL_LINK) : null;
    }
}
