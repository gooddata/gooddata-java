/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GDDateTimeSerializer;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Batch fail status of dataset load.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchFailStatus {

    private final List<FailStatus> failStatuses;
    private final List<String> messages;
    private final String status;
    private final DateTime date;

    @JsonCreator
    private BatchFailStatus(@JsonProperty("uploads") List<FailStatus> failStatuses, @JsonProperty("messages") List<String> messages,
            @JsonProperty("status") String status, @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date) {
        this.failStatuses = failStatuses;
        this.messages = messages;
        this.status = status;
        this.date = date;
    }

    public List<FailStatus> getFailStatuses() {
        return failStatuses;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getStatus() {
        return status;
    }

    @JsonSerialize(using = GDDateTimeSerializer.class)
    public DateTime getDate() {
        return date;
    }
}
