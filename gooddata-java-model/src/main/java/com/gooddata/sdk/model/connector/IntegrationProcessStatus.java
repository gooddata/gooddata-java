/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.model.util.UriHelper;
import com.gooddata.util.GoodDataToStringBuilder;
import com.gooddata.util.ISODateTimeDeserializer;
import com.gooddata.util.ISODateTimeSerializer;
import org.joda.time.DateTime;

import java.util.Map;

import static com.gooddata.util.Validate.notNullState;

/**
 * Connector process (i.e. single ETL run) status used in integration object. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationProcessStatus {

    public static final String URI = "/gdc/projects/{project}/connectors/{connector}/integration/processes/{process}";
    private static final String SELF_LINK = "self";

    private final Status status;
    private final DateTime started;
    private final DateTime finished;
    private final Map<String, String> links;

    @JsonCreator
    protected IntegrationProcessStatus(@JsonProperty("status") Status status,
                                       @JsonProperty("started") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime started,
                                       @JsonProperty("finished") @JsonDeserialize(using = ISODateTimeDeserializer.class) DateTime finished,
                                       @JsonProperty("links") Map<String, String> links) {
        this.status = status;
        this.started = started;
        this.finished = finished;
        this.links = links;
    }

    public Status getStatus() {
        return status;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getStarted() {
        return started;
    }

    @JsonSerialize(using = ISODateTimeSerializer.class)
    public DateTime getFinished() {
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
