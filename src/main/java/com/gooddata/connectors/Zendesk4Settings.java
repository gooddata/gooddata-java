/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.connectors;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import static com.gooddata.Validate.notEmpty;

/**
 * Zendesk 4 connector settings
 */
@JsonTypeName("settings")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Zendesk4Settings implements Settings {

    private String apiUrl;
    private final String type;
    private final String syncTime;
    private final String syncTimeZone;

    public Zendesk4Settings(final String apiUrl) {
        this(apiUrl, null, null, null);
    }

    @JsonCreator
    public Zendesk4Settings(@JsonProperty("apiUrl") String apiUrl, @JsonProperty("type") String type,
                            @JsonProperty("syncTime") String syncTime, @JsonProperty("syncTimeZone") String syncTimeZone) {
        this.apiUrl = notEmpty(apiUrl, "apiUrl");
        this.type = type;
        this.syncTime = syncTime;
        this.syncTimeZone = syncTimeZone;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(final String apiUrl) {
        this.apiUrl = notEmpty(apiUrl, "apiUrl");
    }

    public String getType() {
        return type;
    }

    public String getSyncTime() {
        return syncTime;
    }

    public String getSyncTimeZone() {
        return syncTimeZone;
    }

    @Override
    public Connector getConnector() {
        return Connector.ZENDESK4;
    }
}
