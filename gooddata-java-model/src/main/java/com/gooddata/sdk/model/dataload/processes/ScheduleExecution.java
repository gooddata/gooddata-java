/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.util.ISODateTimeDeserializer;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.gooddata.util.Validate.notNullState;

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

    private DateTime created;
    private String status;
    private String trigger;
    private String processLastDeployedBy;

    private Map<String,String> links;

    public ScheduleExecution() {}

    @JsonCreator
    private ScheduleExecution(@JsonProperty("createdTime") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime created,
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

    public DateTime getCreated() {
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
