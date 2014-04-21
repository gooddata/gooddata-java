package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Fail status of dataset load
 */
public class FailStatus {

    private final String status;
    private final String date; // todo date
    private final ErrorStructure error;

    @JsonCreator
    public FailStatus(@JsonProperty("status") String status, @JsonProperty("date") String date,
                      @JsonProperty("error") ErrorStructure error) {
        this.status = status;
        this.date = date;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public ErrorStructure getError() {
        return error;
    }
}
