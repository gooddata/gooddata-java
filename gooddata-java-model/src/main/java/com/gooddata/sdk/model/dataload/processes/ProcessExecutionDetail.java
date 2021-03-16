/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.gdc.ErrorStructure;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Dataload process execution detail. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("executionDetail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessExecutionDetail {

    private static final String LOG_LINK = "log";
    private static final String SELF_LINK = "self";
    private static final String EXECUTION_LINK = "poll";
    private static final String STATUS_OK = "OK";
    private final String status;

    @ISOZonedDateTime
    private final ZonedDateTime created;
    @ISOZonedDateTime
    private final ZonedDateTime started;
    @ISOZonedDateTime
    private final ZonedDateTime updated;
    @ISOZonedDateTime
    private final ZonedDateTime finished;

    private final ErrorStructure error;
    private final Map<String,String> links;

    @JsonCreator
    private ProcessExecutionDetail(@JsonProperty("status") String status,
                                   @JsonProperty("created") ZonedDateTime created,
                                   @JsonProperty("started") ZonedDateTime started,
                                   @JsonProperty("updated") ZonedDateTime updated,
                                   @JsonProperty("finished") ZonedDateTime finished,
                                   @JsonProperty("error") ErrorStructure error,
                                   @JsonProperty("links") Map<String, String> links) {
        this.status = notEmpty(status, "status");
        this.created = notNull(created, "created");
        this.started = started;
        this.updated = updated;
        this.finished = finished;
        this.error = error;
        this.links = links;
    }

    public String getStatus() {
        return status;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getStarted() {
        return started;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public ZonedDateTime getFinished() {
        return finished;
    }

    public ErrorStructure getError() {
        return error;
    }

    @JsonIgnore
    public String getLogUri() {
        return notNullState(links, "links").get(LOG_LINK);
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getExecutionUri() {
        return notNullState(links, "links").get(EXECUTION_LINK);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return STATUS_OK.equals(status);
    }


    public static URI uriFromExecutionUri(URI executionUri) {
        return URI.create(executionUri.toString() + "/detail");
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
