package com.gooddata.task;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * TODO
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("wTaskStatus")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskStatus {

    private static final String OK = "OK";

    private final String status;
    private final String uri;

    // TODO messages

    @JsonCreator
    public TaskStatus(@JsonProperty("status") String status, @JsonProperty("poll") String uri) {
        this.status = status;
        this.uri = uri;
    }

    public String getStatus() {
        return status;
    }

    public String getUri() {
        return uri;
    }

    public boolean isSuccess() {
        return OK.equals(status);
    }
}
