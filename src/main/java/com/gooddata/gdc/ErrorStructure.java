package com.gooddata.gdc;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorStructure {
    protected final String message;
    protected final String[] parameters;
    protected final String component;
    protected final String errorClass;

    public ErrorStructure(@JsonProperty("errorClass") String errorClass, @JsonProperty("component") String component,
                          @JsonProperty("parameters") String[] parameters, @JsonProperty("message") String message) {
        this.errorClass = errorClass;
        this.component = component;
        this.parameters = parameters;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParameters() {
        return parameters;
    }

    public String getComponent() {
        return component;
    }

    public String getErrorClass() {
        return errorClass;
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
