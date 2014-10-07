package com.gooddata.dataload.processes;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

import java.net.URI;
import java.util.Map;

/**
 * Dataload process execution detail. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionDetail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessExecutionDetail {

    private static final String LOG_LINK = "log";
    private static final String SELF_LINK = "self";
    private static final String EXECUTION_LINK = "poll";
    private static final String STATUS_OK = "OK";
    private final String status;

    // TODO should be dates
    private final String created;
    private final String started;
    private final String updated;
    private final String finished;

    private final ErrorStructure error;
    private final Map<String,String> links;

    @JsonCreator
    private ProcessExecutionDetail(@JsonProperty("status") String status, @JsonProperty("created") String created, @JsonProperty("started") String started,
            @JsonProperty("updated") String updated, @JsonProperty("finished")String finished, @JsonProperty("error") ErrorStructure error,
            @JsonProperty("links") Map<String, String> links) {
        this.status = notEmpty(status, "status");
        this.created = notNull(created, "created");
        this.started = started;
        this.updated = updated;
        this.finished = finished;
        this.error = error;
        this.links = links;
    }

    public String getStatus() {
        return status;
    }

    public String getCreated() {
        return created;
    }

    public String getStarted() {
        return started;
    }

    public String getUpdated() {
        return updated;
    }

    public String getFinished() {
        return finished;
    }

    public ErrorStructure getError() {
        return error;
    }

    @JsonIgnore
    public String getLogLink() {
        return links != null ? links.get(LOG_LINK) : null;
    }

    @JsonIgnore
    public String getUri() {
        return links != null ? links.get(SELF_LINK) : null;
    }

    @JsonIgnore
    public String getExecutionLink() {
        return links != null ? links.get(EXECUTION_LINK) : null;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return STATUS_OK.equals(status);
    }


    public static URI uriFromExecutionUri(URI executionUri) {
        return URI.create(executionUri.toString() + "/detail");
    }
}
