/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connectors;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Connector process
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ConnectorProcess {

    private final Status status;
    private final String started; // todo date
    private final String finished; // todo date

    @JsonCreator
    protected ConnectorProcess(@JsonProperty("status") Status status, @JsonProperty("started") String started,
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

    @JsonIgnore
    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    @JsonIgnore
    public boolean isFailed() {
        return status != null && status.isFailed();
    }
}
