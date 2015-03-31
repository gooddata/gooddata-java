package com.gooddata.gdc;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collection;
import java.util.Collections;

/**
 * Abstract asynchronous task status.
 * Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("wTaskStatus")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskStatus {

    private static final String OK = "OK";
    private static final String RUNNING = "RUNNING";

    private final String status;

    private final String pollUri;

    private final Collection<GdcError> messages;

    @JsonCreator
    private TaskStatus(@JsonProperty("status") String status, @JsonProperty("poll") String pollUri,
                       @JsonProperty("messages") Collection<GdcError> messages) {
        this.status = status;
        this.pollUri = pollUri;
        this.messages = messages;
    }

    public TaskStatus(final String status, final String pollUri) {
        this(status, pollUri, Collections.<GdcError>emptyList());
    }

    public String getStatus() {
        return status;
    }

    public String getPollUri() {
        return pollUri;
    }

    public Collection<GdcError> getMessages() {
        return messages;
    }

    public boolean isSuccess() {
        return OK.equals(status);
    }

    public boolean isRunning() {
        return RUNNING.equals(status);
    }

}
