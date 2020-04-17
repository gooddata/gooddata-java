/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.sdk.common.util.ISOZonedDateTimeDeserializer;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Schedule execution
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("execution")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleExecution {

    public static final String URI = "/gdc/projects/{projectId}/schedules/{scheduleId}/executions/{executionId}";
    private static final Set<String> FINISHED_STATUSES = new HashSet<>(Arrays.asList("OK", "ERROR", "CANCELED", "TIMEOUT"));

    private ZonedDateTime created;
    private String status;
    private String trigger;
    private String processLastDeployedBy;

    private Map<String,String> links;

    public ScheduleExecution() {}

    @JsonCreator
    private ScheduleExecution(@JsonProperty("createdTime") @JsonDeserialize(using = ISOZonedDateTimeDeserializer.class) ZonedDateTime created,
                              @JsonProperty("status") String executionStatus,
                              @JsonProperty("trigger") String trigger,
                              @JsonProperty("processLastDeployedBy") String processLastDeployedBy,
                              @JsonProperty("links") Map<String, String> links) {
        this.created = created;
        this.status = executionStatus;
        this.trigger = trigger;
        this.processLastDeployedBy = processLastDeployedBy;
        this.links = links;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getProcessLastDeployedBy() {
        return processLastDeployedBy;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get("self");
    }

    /**
     *
     * @return whether schedule execution is finished
     */
    @JsonIgnore
    public boolean isFinished() {
        return FINISHED_STATUSES.contains(getStatus());
    }
}
