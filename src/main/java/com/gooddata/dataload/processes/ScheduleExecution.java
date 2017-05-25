/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload.processes;

import static com.gooddata.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.util.ISODateTimeDeserializer;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Schedule execution
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("execution")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScheduleExecution {

    public static final String URI = "/gdc/projects/{projectId}/schedules/{scheduleId}/executions/{executionId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    private static final Set<String> FINISHED_STATUSES = new HashSet<>(Arrays.asList("OK", "ERROR", "CANCELED", "TIMEOUT"));

    private DateTime created;
    private String status;
    private String trigger;
    private String processLastDeployedBy;

    private Map<String,String> links;

    ScheduleExecution() {}

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
