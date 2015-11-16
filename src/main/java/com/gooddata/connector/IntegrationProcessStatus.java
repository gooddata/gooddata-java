/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connector;

import com.gooddata.util.ISODateTimeDeserializer;
import com.gooddata.util.ISODateTimeSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

/**
 * Connector process (i.e. single ETL run) status used in integration object. Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IntegrationProcessStatus {

    public static final String URI = "/gdc/projects/{project}/connectors/{connector}/integration/processes/{process}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    private static final String SELF_LINK = "self";

    private final Status status;
    private final DateTime started;
    private final DateTime finished;
    private Map<String, String> links;

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
        return links != null ? links.get(SELF_LINK): null;
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("process");
    }
}
