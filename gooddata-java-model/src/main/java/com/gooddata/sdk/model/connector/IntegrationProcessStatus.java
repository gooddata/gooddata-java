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
import com.gooddata.sdk.model.util.UriHelper;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;
import com.gooddata.sdk.common.util.ISOZonedDateTime;

import java.time.ZonedDateTime;
import java.util.Map;

import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Connector process (i.e. single ETL run) status used in integration object. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationProcessStatus {

    public static final String URI = "/gdc/projects/{project}/connectors/{connector}/integration/processes/{process}";
    private static final String SELF_LINK = "self";

    private final Status status;
    @ISOZonedDateTime
    private final ZonedDateTime started;
    @ISOZonedDateTime
    private final ZonedDateTime finished;
    private final Map<String, String> links;

    @JsonCreator
    protected IntegrationProcessStatus(@JsonProperty("status") Status status,
                                       @JsonProperty("started") ZonedDateTime started,
                                       @JsonProperty("finished") ZonedDateTime finished,
                                       @JsonProperty("links") Map<String, String> links) {
        this.status = status;
        this.started = started;
        this.finished = finished;
        this.links = links;
    }

    public Status getStatus() {
        return status;
    }

    public ZonedDateTime getStarted() {
        return started;
    }

    public ZonedDateTime getFinished() {
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

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getId() {
        return UriHelper.getLastUriPart(getUri());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

