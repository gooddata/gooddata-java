/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;
import java.util.Optional;

/**
 * Connector reload.
 */
@JsonTypeName("reload")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reload {

    public static final String URL = "/gdc/projects/{project}/connectors/zendesk4/integration/reloads";

    public static final String CHATS_START_TIME_PROPERTY = "chats";
    public static final String AGENT_TIMELINE_START_TIME_PROPERTY = "agentTimeline";

    /**
     * Reload was scheduled but not yet started.
     */
    public static final String STATUS_DO = "DO";

    /**
     * Reload is running.
     */
    public static final String STATUS_RUNNING = "RUNNING";

    /**
     * Reload was successfully finished.
     */
    public static final String STATUS_FINISHED = "FINISHED";

    /**
     * Reload wasn't started because reload window was missed.
     */
    public static final String STATUS_MISSED = "MISSED";

    /**
     * Reload finished with error.
     */
    public static final String STATUS_ERROR = "ERROR";

    private static final String SELF_LINK = "self";
    private static final String PROCESS_LINK = "process";
    private static final String INTEGRATION_LINK = "integration";


    private final Integer id;

    private final Map<String, Long> startTimes;

    private final String status;

    private final String processId;

    private final Map<String, String> links;

    public Reload(Map<String, Long> startTimes) {
        this(null, startTimes, null, null, null);
    }

    @JsonCreator
    public Reload(
            @JsonProperty("id") final Integer id,
            @JsonProperty("startTimes") final Map<String, Long> startTimes,
            @JsonProperty("status") final String status,
            @JsonProperty("processId") final String processId,
            @JsonProperty("links") final Map<String, String> links) {
        this.id = id;
        this.startTimes = startTimes;
        this.status = status;
        this.processId = processId;
        this.links = links;
    }

    public Integer getId() {
        return id;
    }

    public Map<String, Long> getStartTimes() {
        return startTimes;
    }

    @JsonIgnore
    public Long getChatsStartTime() {
        return startTimes.get(CHATS_START_TIME_PROPERTY);
    }

    @JsonIgnore
    public Long getAgentTimelineStartTime() {
        return startTimes.get(AGENT_TIMELINE_START_TIME_PROPERTY);
    }

    public String getStatus() {
        return status;
    }

    public String getProcessId() {
        return processId;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * @return URI to itself.
     */
    @JsonIgnore
    public Optional<String> getUri() {
        return getLink(SELF_LINK);
    }

    /**
     * @return URI to running process. Is empty if reload wasn't started yet.
     */
    @JsonIgnore
    public Optional<String> getProcessUri() {
        return getLink(PROCESS_LINK);
    }

    /**
     * @return URI to integration.
     */
    @JsonIgnore
    public Optional<String> getIntegrationUri() {
        return getLink(INTEGRATION_LINK);
    }

    private Optional<String> getLink(final String linkName) {
        return links != null ? Optional.ofNullable(links.get(linkName)) : Optional.empty();
    }
}

