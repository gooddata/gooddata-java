package com.gooddata.gdc;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.gooddata.Validate.notNull;
import static java.util.Arrays.copyOf;

/**
 * Error structure (for embedding).
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorStructure {
    protected final String message;
    protected final String[] parameters;
    protected final String component;
    protected final String errorClass;
    protected final String errorCode;
    protected final String errorId;
    protected final String trace;
    protected final String requestId;

    @JsonCreator
    protected ErrorStructure(@JsonProperty("errorClass") String errorClass, @JsonProperty("component") String component,
                             @JsonProperty("parameters") String[] parameters, @JsonProperty("message") String message,
                             @JsonProperty("errorCode") String errorCode, @JsonProperty("errorId") String errorId,
                             @JsonProperty("trace") String trace, @JsonProperty("requestId") String requestId) {
        this.errorClass = errorClass;
        this.component = component;
        this.parameters = parameters;
        this.message = notNull(message, "message");
        this.errorCode = errorCode;
        this.errorId = errorId;
        this.trace = trace;
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParameters() {
        return parameters == null ? null : copyOf(parameters, parameters.length);
    }

    public String getComponent() {
        return component;
    }

    public String getErrorClass() {
        return errorClass;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getTrace() {
        return trace;
    }

    public String getRequestId() {
        return requestId;
    }

    @JsonIgnore
    public String getFormattedMessage() {
        return message == null ? null : String.format(message, parameters);
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
