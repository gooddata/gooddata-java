package com.gooddata.connectors;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Process status
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Status {
    private final String code;
    private final String detail;
    private final String description;

    @JsonCreator
    Status(@JsonProperty("code") String code, @JsonProperty("detail") String detail,
           @JsonProperty("description") String description) {
        this.code = code;
        this.detail = detail;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public boolean isFinished() {
        return "SYNCHRONIZED".equalsIgnoreCase(code) || isFailed();
    }

    @JsonIgnore
    public boolean isFailed() {
        return "ERROR".equalsIgnoreCase(code) || "USER_ERROR".equalsIgnoreCase(code);
    }
}
