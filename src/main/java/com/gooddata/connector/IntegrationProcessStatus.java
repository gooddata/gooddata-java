/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Connector process (i.e. single ETL run) status used in integration object. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IntegrationProcessStatus {

    private final Status status;
    private final String started; // todo date
    private final String finished; // todo date

    @JsonCreator
    protected IntegrationProcessStatus(@JsonProperty("status") Status status, @JsonProperty("started") String started,
                                       @JsonProperty("finished") String finished) {
        this.status = status;
        this.started = started;
        this.finished = finished;
    }

    public Status getStatus() {
        return status;
    }

    public String getStarted() {
        return started;
    }

    public String getFinished() {
        return finished;
    }

    /**
     * Returns true when the connector process has already finished (no matter if it was successful).
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the connector process has already finished, false otherwise
     */
    @JsonIgnore
    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    /**
     * Returns true when the connector process failed.
     * NOTE: It also returns false in case of inability to resolve the code (e.g. API change)
     *
     * @return true when the connector process failed, false otherwise
     */
    @JsonIgnore
    public boolean isFailed() {
        return status != null && status.isFailed();
    }
}
