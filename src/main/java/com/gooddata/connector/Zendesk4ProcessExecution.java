/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import static com.gooddata.connector.ConnectorType.ZENDESK4;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.ISODateTimeSerializer;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.TreeMap;

/**
 * Zendesk 4 (Insights) connector process execution (i.e. definition for single ETL run). Serialization only.
 */
public class Zendesk4ProcessExecution implements ProcessExecution {

    private Boolean incremental;

    private Map<String, DateTime> startTimes;

    @Override
    public ConnectorType getConnectorType() {
        return ZENDESK4;
    }

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(final Boolean incremental) {
        this.incremental = incremental;
    }

    @JsonAnyGetter
    @JsonSerialize(contentUsing = ISODateTimeSerializer.class)
    public Map<String, DateTime> getStartTimes() {
        return startTimes;
    }


    public void setStartTime(final String resource, final DateTime startTime) {
        notEmpty(resource, "resource can't be empty");
        notNull(startTime, "startTime can't be null");

        startTimes = startTimes == null ? new TreeMap<String, DateTime>() : startTimes;

        startTimes.put(resource + "StartDate", startTime);
    }
}
